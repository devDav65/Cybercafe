package com.example.monprojet.service;

import com.example.monprojet.model.Packet;
import com.example.monprojet.model.Reglefiltre;
import com.example.monprojet.model.Journal;
import com.example.monprojet.dao.ReglefiltreDAO;
import com.example.monprojet.dao.JournalDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

/**
 * Service qui lit périodiquement un fichier (ou dossier) JSON contenant des
 * paquets
 * simulés, applique les règles de filtrage et enregistre les évènements dans la
 * table journal.
 */
public class PacketProcessor {

    private final ObjectMapper mapper;
    private final ReglefiltreDAO regleDAO = new ReglefiltreDAO();
    private final JournalDAO journalDAO = new JournalDAO();

    // Cache pour éviter de logguer plusieurs fois le même blocage (clé =
    // src|dest|proto)
    private final ConcurrentHashMap<String, Long> blockedCache = new ConcurrentHashMap<>();
    private final long dedupeWindowMillis = TimeUnit.SECONDS.toMillis(30); // 30 s de fenêtre anti-doublons

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public PacketProcessor() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Lance la surveillance d’un fichier ou d’un dossier.
     * 
     * @param dirOrFile       chemin du fichier ou du dossier contenant des .json
     * @param intervalSeconds intervalle entre deux analyses
     */
    public void startPolling(Path dirOrFile, long intervalSeconds) {
        System.out.println(LocalDateTime.now() + " - PacketProcessor started");
        executor.scheduleWithFixedDelay(() -> {
            try {
                pollFile(dirOrFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cleanupCache();
        }, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    /** Arrête proprement le scheduler */
    public void stop() {
        executor.shutdownNow();
    }

    /** Supprime les entrées de cache expirées */
    private void cleanupCache() {
        long now = System.currentTimeMillis();
        blockedCache.entrySet().removeIf(e -> e.getValue() < now);
    }

    /** Parcourt un fichier ou un dossier et traite les paquets */
    public void pollFile(Path file) throws IOException {
        if (!Files.exists(file))
            return;

        if (Files.isDirectory(file)) {
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(file, "*.json")) {
                for (Path p : ds) {
                    processSingleFile(p);
                }
            }
        } else {
            processSingleFile(file);
        }
    }

    /** Traite un fichier JSON unique contenant un tableau de paquets */
    private void processSingleFile(Path p) {
        try {
            Packet[] packets = mapper.readValue(p.toFile(), Packet[].class);
            List<Reglefiltre> rules = regleDAO.getAllRegles();

            for (Packet pkt : packets) {
                handlePacket(pkt, rules);
            }

            // Exemple : déplacer ou renommer le fichier traité (optionnel)
            // Files.move(p, p.resolveSibling(p.getFileName() + ".done"),
            // StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /** Gère un paquet individuel : filtrage et enregistrement éventuel */
    private void handlePacket(Packet pkt, List<Reglefiltre> rules) {
        boolean blocked = isBlocked(pkt, rules);
        String key = pkt.getIpsource() + "|" + pkt.getIpdestination() + "|" + pkt.getProtocole();

        if (blocked) {
            long now = System.currentTimeMillis();
            Long expiry = blockedCache.get(key);

            if (expiry == null || expiry < now) {
                String msg = "Blocked packet " + pkt.getIpsource() + " -> " + pkt.getIpdestination();
                System.out.println(LocalDateTime.now() + " - " + msg);

                // Enregistrement dans la base de données
                Journal j = new Journal(
                        0,
                        pkt.getIpsource(),
                        pkt.getIpdestination(),
                        pkt.getProtocole(),
                        pkt.getTimestamp().toLocalTime(),
                        "BLOCK");
                journalDAO.ajouterJournal(j);

                // mémorise le blocage pour éviter les doublons immédiats
                blockedCache.put(key, now + dedupeWindowMillis);
            }
        } else {
            // Ici tu peux aussi logguer les paquets autorisés si besoin
            // journalDAO.ajouterJournal(new Journal(..., "ALLOW"));
        }
    }

    /** Vérifie si un paquet doit être bloqué en fonction des règles */
    private boolean isBlocked(Packet pkt, List<Reglefiltre> rules) {
        for (Reglefiltre r : rules) {
            boolean ipMatch = r.getIp() == null || r.getIp().isBlank()
                    || pkt.getIpsource().equals(r.getIp())
                    || pkt.getIpdestination().equals(r.getIp());
            boolean protoMatch = r.getProtocole() == null || r.getProtocole().isBlank()
                    || pkt.getProtocole().equalsIgnoreCase(r.getProtocole());

            if (ipMatch && protoMatch) {
                return "block".equalsIgnoreCase(r.getAction()) || "deny".equalsIgnoreCase(r.getAction());
            }
        }
        return false;
    }
}

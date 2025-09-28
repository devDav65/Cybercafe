package com.example.monprojet.service;

import com.example.monprojet.model.Packet;
import com.example.monprojet.model.Journal;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * Lit un fichier JSONL de paquets et les enregistre dans la DB.
 */
public class PacketProcessor {

    private final ObjectMapper mapper;
    private final JournalService journalService = new JournalService();

    private final ConcurrentHashMap<String, Long> blockedCache = new ConcurrentHashMap<>();
    private final long dedupeWindowMillis = TimeUnit.SECONDS.toMillis(30);

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true); // le thread ne bloque plus la JVM
        return t;
    });

    public PacketProcessor() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Démarre la surveillance du fichier JSONL
     * 
     * @param file            chemin du fichier
     * @param intervalSeconds intervalle entre deux lectures
     */
    private volatile boolean running = true; // flag d'arrêt

    public void startPolling(Path file, long intervalSeconds) {
        System.out.println(LocalDateTime.now() + " - PacketProcessor started");

        executor.scheduleWithFixedDelay(() -> {
            if (!running)
                return; // ne rien faire si on a arrêté
            try {
                pollFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cleanupCache();
        }, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    public void stop() {
        running = false; // stoppe la boucle
        executor.shutdownNow(); // interrompt toutes les tâches
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("Scheduler did not terminate in time. Forcing shutdown.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        blockedCache.clear();
        System.out.println(LocalDateTime.now() + " - PacketProcessor stopped");
    }

    /** Supprime les entrées de cache expirées */
    private void cleanupCache() {
        long now = System.currentTimeMillis();
        blockedCache.entrySet().removeIf(e -> e.getValue() < now);
    }

    /** Lit le fichier et traite chaque ligne JSON */
    private void pollFile(Path file) throws IOException {
        if (!Files.exists(file))
            return;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Packet pkt = mapper.readValue(line, Packet.class);
                    handlePacket(pkt);
                } catch (Exception ex) {
                    System.err.println("Erreur parsing JSON: " + line);
                    ex.printStackTrace();
                }
            }
        }
    }

    /** Traite un paquet : insertion dans DB si pas doublon récent */
    private void handlePacket(Packet pkt) {
        String key = pkt.getIpsource() + "|" + pkt.getIpdestination() + "|" + pkt.getProtocole();
        long now = System.currentTimeMillis();
        Long expiry = blockedCache.get(key);

        if (expiry == null || expiry < now) {
            // On enregistre le paquet dans la DB
            Journal j = new Journal(
                    0,
                    pkt.getIpsource(),
                    pkt.getIpdestination(),
                    pkt.getProtocole(),
                    pkt.getTimestamp().toLocalTime(),
                    "ALLOW" // ou "BLOCK" si tu veux appliquer des règles
            );
            journalService.enregistrerEntree(
                    j.getIpsource(),
                    j.getIpdestination(),
                    j.getProtocole(),
                    j.getHorlotage(),
                    j.getAction());

            // ajoute dans le cache anti-doublons
            blockedCache.put(key, now + dedupeWindowMillis);

            // System.out.println(LocalDateTime.now() + " - Packet enregistré: " + key);
        }
    }
}

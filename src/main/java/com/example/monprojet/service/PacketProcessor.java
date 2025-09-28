package com.example.monprojet.service;

import com.example.monprojet.model.Packet;
import com.example.monprojet.model.Journal;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

/**
 * Lit un fichier JSONL de paquets et les enregistre dans la DB.
 */
public class PacketProcessor {

    private final ObjectMapper mapper;
    private final JournalService journalService = new JournalService();
    private final ConcurrentHashMap<String, Long> blockedCache = new ConcurrentHashMap<>();
    private final long dedupeWindowMillis = TimeUnit.SECONDS.toMillis(30);

    private volatile boolean running = false; // flag d'arrêt
    private Thread pollingThread;

    public PacketProcessor() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /** Démarre le polling en arrière-plan */
    public void startPolling(Path file, long intervalSeconds) {
        if (running)
            return;
        running = true;

        pollingThread = new Thread(() -> {
            System.out.println(LocalDateTime.now() + " - PacketProcessor started");
            while (running) {
                try {
                    pollFile(file);
                    cleanupCache();
                    Thread.sleep(intervalSeconds * 1000);
                } catch (InterruptedException e) {
                    // Arrêt demandé
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(LocalDateTime.now() + " - PacketProcessor stopped");
        });
        pollingThread.setDaemon(true); // ne bloque pas la JVM
        pollingThread.start();
    }

    /** Stoppe proprement le polling */
    public void stop() {
        running = false;
        if (pollingThread != null) {
            pollingThread.interrupt();
            try {
                pollingThread.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        blockedCache.clear();
    }

    /** Nettoie les entrées expirées du cache */
    private void cleanupCache() {
        long now = System.currentTimeMillis();
        blockedCache.entrySet().removeIf(e -> e.getValue() < now);
    }

    /** Lit le fichier et traite chaque paquet */
    private void pollFile(Path file) throws IOException {
        if (!Files.exists(file))
            return;

        List<String> lines = Files.readAllLines(file); // lecture non-bloquante
        for (String line : lines) {
            if (!running)
                break;
            try {
                Packet pkt = mapper.readValue(line, Packet.class);
                handlePacket(pkt);
            } catch (Exception ex) {
                System.err.println("Erreur parsing JSON: " + line);
                ex.printStackTrace();
            }
        }
    }

    /** Traite un paquet */
    private void handlePacket(Packet pkt) {
        String key = pkt.getIpsource() + "|" + pkt.getIpdestination() + "|" + pkt.getProtocole();
        long now = System.currentTimeMillis();
        Long expiry = blockedCache.get(key);

        if (expiry == null || expiry < now) {
            Journal j = new Journal(
                    0,
                    pkt.getIpsource(),
                    pkt.getIpdestination(),
                    pkt.getProtocole(),
                    pkt.getTimestamp().toLocalTime(),
                    "ALLOW");
            journalService.enregistrerEntree(
                    j.getIpsource(),
                    j.getIpdestination(),
                    j.getProtocole(),
                    j.getHorlotage(),
                    j.getAction());
            blockedCache.put(key, now + dedupeWindowMillis);
        }
    }
}

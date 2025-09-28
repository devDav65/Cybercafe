package com.example.monprojet.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.monprojet.dao.DemandeDAO;
import com.example.monprojet.dao.PosteDAO;
import com.example.monprojet.model.Demande;
import com.example.monprojet.model.Poste;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Vérifie périodiquement les demandes en cours.
 * - Notifie quand la durée est écoulée
 * - Remet le poste en "disponible"
 * - Supprime la demande de la base
 */
public class SessionScheduler {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final DemandeDAO demandeDAO = new DemandeDAO();
    private final PosteDAO posteDAO = new PosteDAO();

    /**
     * Lance la vérification toutes les 60 secondes.
     */
    public void start() {
        scheduler.scheduleAtFixedRate(this::checkSessions, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Arrête le scheduler (à appeler quand l'appli se ferme).
     */
    public void stop() {
        scheduler.shutdownNow();
    }

    /**
     * Parcourt les demandes et clôture celles qui ont dépassé leur durée.
     */
    private void checkSessions() {
        try {
            List<Demande> toutes = demandeDAO.getAllDemandes();
            LocalDateTime now = LocalDateTime.now();

            for (Demande d : toutes) {
                LocalDateTime fin = d.getDateHeure().plusMinutes(d.getDuree());
                if (!now.isBefore(fin)) { // now >= fin
                    double prix = (d.getDuree() / 60.0) * 100.0; // tarif horaire 100 F CFA

                    // 1️⃣ Notification JavaFX (sur le thread UI)
                    notifyUser("La session de " + d.getUtilisateur().getNom()
                            + " est terminée.\nMontant à payer : " + prix + " F CFA");

                    // 2️⃣ Mise à jour du poste
                    Poste p = d.getPoste();
                    if (p != null) {
                        p.setEtat("disponible");
                        posteDAO.updatePoste(p);
                    }

                    // 3️⃣ Suppression de la demande expirée
                    demandeDAO.supprimerDemande(d.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche une alerte JavaFX depuis un thread non-UI.
     */
    private void notifyUser(String msg) {
        Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, msg).show());
    }
}

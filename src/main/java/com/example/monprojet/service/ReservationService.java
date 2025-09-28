package com.example.monprojet.service;

import com.example.monprojet.dao.ReservationDAO;
import com.example.monprojet.dao.PosteDAO;
import com.example.monprojet.dao.UtilisateurDAO;
import com.example.monprojet.model.Poste;
import com.example.monprojet.model.Reservation;
import com.example.monprojet.model.Utilisateur;
import com.example.monprojet.Util.Logger;

import java.time.*;
import java.util.List;

public class ReservationService {

    private final ReservationDAO reservationDAO;
    private final PosteDAO posteDAO;
    private final UtilisateurDAO utilisateurDAO;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.posteDAO = new PosteDAO();
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public boolean creerReservation(int utilisateurId, int posteId, LocalDate dateReservation, LocalTime heure) {
        Utilisateur u = utilisateurDAO.getUtilisateurById(utilisateurId);
        Poste p = posteDAO.getPosteById(posteId);

        if (u == null || p == null) {
            Logger.logInfo("Utilisateur ou poste introuvable.");
            return false;
        }

        if (!"disponible".equalsIgnoreCase(p.getEtat())) {
            Logger.logInfo("Poste non disponible.");
            return false;
        }

        List<Reservation> toutes = reservationDAO.getAllReservations();
        for (Reservation r : toutes) {
            if (r.getPoste().getId() == posteId && r.getDateReservation().equals(dateReservation)
                    && r.getHeure().equals(heure)) {
                Logger.logInfo("Conflit: poste déjà réservé.");
                return false;
            }
        }

        Reservation r = new Reservation(0, dateReservation, heure, u, p);
        reservationDAO.ajouterReservation(r);

        p.setEtat("occupe");
        posteDAO.updatePoste(p);

        Logger.logInfo("Réservation créée pour utilisateur " + utilisateurId + " poste " + posteId);
        return true;
    }

    public List<Reservation> listerReservations() {
        return reservationDAO.getAllReservations();
    }

    public void annulerReservation(int reservationId) {
        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r != null) {
            Poste p = r.getPoste();
            p.setEtat("disponible");
            posteDAO.updatePoste(p);
            reservationDAO.deleteReservation(reservationId);
            Logger.logInfo("Réservation annulée id=" + reservationId);
        }
    }
}

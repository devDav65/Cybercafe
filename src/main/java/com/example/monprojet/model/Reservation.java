package com.example.monprojet.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {
    private int id;
    private LocalDate dateReservation;
    private LocalTime heure;
    private Utilisateur utilisateur;
    private Poste poste;

    public Reservation(int id, LocalDate dateReservation, LocalTime heure, Utilisateur utilisateur, Poste poste) {
        this.id = id;
        this.dateReservation = dateReservation;
        this.heure = heure;
        this.utilisateur = utilisateur;
        this.poste = poste;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }
}

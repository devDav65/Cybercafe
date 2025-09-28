package com.example.monprojet.model;

import java.time.LocalDateTime;

public class Demande {
    private int id;
    private int duree;
    private LocalDateTime dateHeure;
    private Utilisateur utilisateur;
    private Poste poste;

    public Demande(int id, int duree, LocalDateTime dateHeure, Utilisateur utilisateur, Poste poste) {
        this.id = id;
        this.duree = duree;
        this.dateHeure = dateHeure;
        this.utilisateur = utilisateur;
        this.poste = poste;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
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

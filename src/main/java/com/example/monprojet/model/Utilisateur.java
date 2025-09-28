package com.example.monprojet.model;

import java.time.LocalDate;

public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String password;
    private LocalDate dateAbonne;

    public Utilisateur(int id, String nom, String prenom, String password, LocalDate dateAbonne) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.dateAbonne = dateAbonne;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateAbonne() {
        return dateAbonne;
    }

    public void setDateAbonne(LocalDate dateAbonne) {
        this.dateAbonne = dateAbonne;
    }
}

package com.example.monprojet.model;

public class Poste {
    private int id;
    private String reference;
    private String adresseIp;
    private String emplacement;
    private String etat; // valeurs possibles : disponible, occupe, maintenance

    public Poste(int id, String reference, String adresseIp, String emplacement, String etat) {
        this.id = id;
        this.reference = reference;
        this.adresseIp = adresseIp;
        this.emplacement = emplacement;
        this.etat = etat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAdresseIp() {
        return adresseIp;
    }

    public void setAdresseIp(String adresseIp) {
        this.adresseIp = adresseIp;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
}

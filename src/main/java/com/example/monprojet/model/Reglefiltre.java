package com.example.monprojet.model;

public class Reglefiltre {
    private int id_filtre;
    private String ip;
    private String protocole;
    private String action;

    public Reglefiltre(int id_filtre, String ip, String protocole, String action) {
        this.id_filtre = id_filtre;
        this.ip = ip;
        this.protocole = protocole;
        this.action = action;
    }

    public int getId_filtre() {
        return id_filtre;
    }

    public void setId_filtre(int id_filtre) {
        this.id_filtre = id_filtre;
    }

    public String getIp() {
        return ip;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public String getProtocole() {
        return protocole;
    }

    public void setProtocole(String protocole) {
        this.protocole = protocole;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
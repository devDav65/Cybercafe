package com.example.monprojet.model;

import java.time.LocalTime;

public class Journal {
    private int id_journal;
    private String ipsource;
    private String ipdestination;
    private String protocole;
    private LocalTime horlotage;
    private String action;

    public Journal(int id_journal, String ipsource, String ipdestination, String protocole, LocalTime horlotage,
            String action) {
        this.id_journal = id_journal;
        this.ipsource = ipsource;
        this.ipdestination = ipdestination;
        this.protocole = protocole;
        this.horlotage = horlotage;
        this.action = action;
    }

    public int getId_journal() {
        return id_journal;
    }

    public void setId_journal(int id_journal) {
        this.id_journal = id_journal;
    }

    public String getIpsource() {
        return ipsource;
    }

    public void setIpsource(String ipsource) {
        this.ipsource = ipsource;
    }

    public String getIpdestination() {
        return ipdestination;
    }

    public void setIpdestination(String ipdestination) {
        this.ipdestination = ipdestination;
    }

    public String getProtocole() {
        return protocole;
    }

    public void setProtocole(String protocole) {
        this.protocole = protocole;
    }

    public LocalTime getHorlotage() {
        return horlotage;
    }

    public void setHorlotage(LocalTime horlotage) {
        this.horlotage = horlotage;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}

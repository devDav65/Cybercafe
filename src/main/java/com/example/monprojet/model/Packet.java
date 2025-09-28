package com.example.monprojet.model;

import java.time.LocalDateTime;

public class Packet {
    private String ipsource;
    private String ipdestination;
    private String protocole;
    private int length;
    private String payload;
    private LocalDateTime timestamp;

    // constructeur, getters, setters

    public Packet() {
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

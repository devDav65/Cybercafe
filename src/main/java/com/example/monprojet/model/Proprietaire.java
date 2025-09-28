package com.example.monprojet.model;

public class Proprietaire {
    private int id;
    private String username;
    private String password; // hashé
    private String pinCode; // hashé aussi

    public Proprietaire(int id, String username, String password, String pinCode) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.pinCode = pinCode;
    }

    public Proprietaire(String username, String password, String pinCode) {
        this(0, username, password, pinCode);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }
}

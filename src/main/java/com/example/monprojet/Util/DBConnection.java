package com.example.monprojet.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/cybercafe";
    private static final String USER = "admin";
    private static final String PASSWORD = "david1465.";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            System.out.println("✅ Connexion réussie à la base de données !");
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ Erreur de connexion : " + e.getMessage());
        }
    }
}

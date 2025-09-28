package com.example.monprojet.dao;

import com.example.monprojet.Util.DBConnection;
import com.example.monprojet.model.Proprietaire;

import java.sql.*;

public class ProprietaireDAO {

    // ➕ Ajouter un propriétaire
    public void ajouterProprietaire(Proprietaire p) {
        String sql = "INSERT INTO proprietaire (username, password, pin_code) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getUsername());
            stmt.setString(2, p.getPassword()); // déjà hashé
            stmt.setString(3, p.getPinCode()); // déjà hashé
            stmt.executeUpdate();
            System.out.println("✅ Propriétaire ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur ajout propriétaire : " + e.getMessage());
        }
    }

    // 🔍 Récupérer un propriétaire par son username
    public Proprietaire getByUsername(String username) {
        String sql = "SELECT * FROM proprietaire WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Proprietaire(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("pin_code"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lecture propriétaire : " + e.getMessage());
        }
        return null;
    }

}

package com.example.monprojet.service;

import com.example.monprojet.dao.ProprietaireDAO;
import com.example.monprojet.model.Proprietaire;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.monprojet.Util.DBConnection;
import com.example.monprojet.Util.PasswordUtils;

public class ProprietaireService {

    private ProprietaireDAO proprietaireDAO = new ProprietaireDAO();

    public void registerProprietaire(String username, String password, String pinCode) {
        String hashedPassword = PasswordUtils.hashPassword(password);
        String hashedPin = PasswordUtils.hashPassword(pinCode);

        Proprietaire proprietaire = new Proprietaire(username, hashedPassword, hashedPin);
        proprietaireDAO.ajouterProprietaire(proprietaire);
    }

    public boolean login(String username, String password, String pinCode) {
        Proprietaire p = proprietaireDAO.getByUsername(username);

        if (p == null) {
            System.out.println("❌ Utilisateur introuvable !");
            return false;
        }

        // Vérifier password et pin (hashés)
        boolean passwordMatch = PasswordUtils.checkPassword(password, p.getPassword());
        boolean pinMatch = PasswordUtils.checkPassword(pinCode, p.getPinCode());

        if (passwordMatch && pinMatch) {
            System.out.println("✅ Connexion réussie du propriétaire !");
            return true;
        } else {
            System.out.println("❌ Identifiants incorrects !");
            return false;
        }
    }

    public boolean enregistrer(Proprietaire p) {
        // 1️⃣ Vérifier si le username existe déjà
        String checkSql = "SELECT id FROM proprietaire WHERE username = ?";
        String insertSql = "INSERT INTO proprietaire (username, password, pin_code) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, p.getUsername());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Un utilisateur avec ce nom existe déjà
                    return false;
                }
            }

            // 2️⃣ Hacher le mot de passe si besoin
            String hashed = p.getPassword();
            // on vérifie si la chaîne ressemble déjà à un hash bcrypt (commence par $2…)
            if (hashed == null || !hashed.startsWith("$2")) {
                hashed = PasswordUtils.hashPassword(p.getPassword());
            }

            // 3️⃣ Insérer le nouveau propriétaire
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, p.getUsername());
                insertStmt.setString(2, hashed);
                insertStmt.setString(3, p.getPinCode());
                int rows = insertStmt.executeUpdate();
                return rows > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Proprietaire authentifier(String username, String password, String pin) {
        String sql = "SELECT * FROM proprietaire WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hash = rs.getString("password");
                String dbPin = rs.getString("pin_code");

                if (PasswordUtils.checkPassword(password, hash) && pin.equals(dbPin)) {
                    return new Proprietaire(
                            rs.getInt("id"),
                            rs.getString("username"),
                            hash,
                            dbPin);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}

package com.example.monprojet.dao;

import com.example.monprojet.Util.DBConnection;
import com.example.monprojet.model.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    // ➕ CREATE : ajouter un utilisateur
    public void ajouterUtilisateur(Utilisateur u) {
        String sql = "INSERT INTO utilisateurs (nom, prenom, password, date_abonne) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, u.getNom());
            stmt.setString(2, u.getPrenom());
            stmt.setString(3, u.getPassword());
            stmt.setDate(4, Date.valueOf(u.getDateAbonne()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    u.setId(rs.getInt(1));
                }
            }

            System.out.println("✅ Utilisateur ajouté !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur ajout utilisateur : " + e.getMessage());
        }
    }

    // 📖 READ : récupérer tous les utilisateurs
    public List<Utilisateur> getUtilisateurs() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Utilisateur u = new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("password"),
                        rs.getDate("date_abonne").toLocalDate());
                liste.add(u);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lecture utilisateurs : " + e.getMessage());
        }
        return liste;
    }

    // 📖 READ : récupérer un utilisateur par ID
    public Utilisateur getUtilisateurById(int id) {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("password"),
                            rs.getDate("date_abonne").toLocalDate());
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lecture utilisateur par ID : " + e.getMessage());
        }
        return null;
    }

    // ✏️ UPDATE : modifier un utilisateur
    public void updateUtilisateur(Utilisateur u) {
        String sql = "UPDATE utilisateurs SET nom = ?, prenom = ?, password = ?, date_abonne = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getNom());
            stmt.setString(2, u.getPrenom());
            stmt.setString(3, u.getPassword());
            stmt.setDate(4, Date.valueOf(u.getDateAbonne()));
            stmt.setInt(5, u.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Utilisateur mis à jour !");
            } else {
                System.out.println("⚠️ Aucun utilisateur trouvé avec l’ID " + u.getId());
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur mise à jour utilisateur : " + e.getMessage());
        }
    }

    // 🗑️ DELETE : supprimer un utilisateur
    public void deleteUtilisateur(int id) {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Utilisateur supprimé !");
            } else {
                System.out.println("⚠️ Aucun utilisateur trouvé avec l’ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur suppression utilisateur : " + e.getMessage());
        }
    }

    public List<Utilisateur> searchUtilisateurs(String keyword) {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE nom LIKE ? OR prenom LIKE ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("password"),
                        rs.getDate("date_abonne").toLocalDate()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}

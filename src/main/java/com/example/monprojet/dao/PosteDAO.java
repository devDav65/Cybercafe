package com.example.monprojet.dao;

import com.example.monprojet.Util.DBConnection;
import com.example.monprojet.model.Poste;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PosteDAO {

    // ➕ CREATE : ajouter un poste
    public void ajouterPoste(Poste p) {
        String sql = "INSERT INTO postes (reference, adresse_ip, emplacement, etat) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getReference());
            stmt.setString(2, p.getAdresseIp());
            stmt.setString(3, p.getEmplacement());
            stmt.setString(4, p.getEtat());

            stmt.executeUpdate();

            // 🔑 Récupération de l’ID auto-généré
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }

            System.out.println("✅ Poste ajouté !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur ajout poste : " + e.getMessage());
        }
    }

    // 📖 READ : récupérer tous les postes
    public List<Poste> getAllPostes() {
        List<Poste> liste = new ArrayList<>();
        String sql = "SELECT * FROM postes";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Poste p = new Poste(
                        rs.getInt("id"),
                        rs.getString("reference"),
                        rs.getString("adresse_ip"),
                        rs.getString("emplacement"),
                        rs.getString("etat"));
                liste.add(p);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lecture postes : " + e.getMessage());
        }
        return liste;
    }

    // 📖 READ : récupérer un poste par ID
    public Poste getPosteById(int id) {
        String sql = "SELECT * FROM postes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Poste(
                            rs.getInt("id"),
                            rs.getString("reference"),
                            rs.getString("adresse_ip"),
                            rs.getString("emplacement"),
                            rs.getString("etat"));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lecture poste par ID : " + e.getMessage());
        }
        return null;
    }

    // ✏️ UPDATE : modifier un poste
    public void updatePoste(Poste p) {
        String sql = "UPDATE postes SET reference = ?, adresse_ip = ?, emplacement = ?, etat = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getReference());
            stmt.setString(2, p.getAdresseIp());
            stmt.setString(3, p.getEmplacement());
            stmt.setString(4, p.getEtat());
            stmt.setInt(5, p.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Poste mis à jour !");
            } else {
                System.out.println("⚠️ Aucun poste trouvé avec l’ID " + p.getId());
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur mise à jour poste : " + e.getMessage());
        }
    }

    // 🗑️ DELETE : supprimer un poste
    public void deletePoste(int id) {
        String sql = "DELETE FROM postes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Poste supprimé !");
            } else {
                System.out.println("⚠️ Aucun poste trouvé avec l’ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur suppression poste : " + e.getMessage());
        }
    }

    public List<Poste> searchPostes(String keyword) {
        List<Poste> list = new ArrayList<>();
        String sql = "SELECT * FROM postes WHERE reference LIKE ? OR adresse_ip LIKE ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Poste(
                            rs.getInt("id"),
                            rs.getString("reference"),
                            rs.getString("adresse_ip"),
                            rs.getString("emplacement"),
                            rs.getString("etat")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}

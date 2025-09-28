package com.example.monprojet.dao;

import com.example.monprojet.Util.DBConnection;
import com.example.monprojet.model.Reglefiltre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReglefiltreDAO {

    /** ➕ CREATE : ajouter une règle */
    public void ajouterRegle(Reglefiltre r) {
        String sql = "INSERT INTO regles_filtre (ip, protocole, action) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, r.getIp());
            stmt.setString(2, r.getProtocole());
            stmt.setString(3, r.getAction());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next())
                    r.setId_filtre(rs.getInt(1));
            }
            System.out.println("✅ Règle de filtre ajoutée !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur ajout règle : " + e.getMessage());
        }
    }

    /** 📖 READ : toutes les règles */
    public List<Reglefiltre> getAllRegles() {
        List<Reglefiltre> liste = new ArrayList<>();
        String sql = "SELECT * FROM regles_filtre";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(new Reglefiltre(
                        rs.getInt("id_filtre"),
                        rs.getString("ip"),
                        rs.getString("protocole"),
                        rs.getString("action")));
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lecture règles : " + e.getMessage());
        }
        return liste;
    }

    /** ✏️ UPDATE */
    public void updateRegle(Reglefiltre r) {
        String sql = "UPDATE regles_filtre SET ip=?, protocole=?, action=? WHERE id_filtre=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, r.getIp());
            stmt.setString(2, r.getProtocole());
            stmt.setString(3, r.getAction());
            stmt.setInt(4, r.getId_filtre());
            stmt.executeUpdate();
            System.out.println("✅ Règle mise à jour !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur mise à jour règle : " + e.getMessage());
        }
    }

    /** 🗑️ DELETE */
    public void deleteRegle(int id) {
        String sql = "DELETE FROM regles_filtre WHERE id_filtre=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✅ Règle supprimée !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur suppression règle : " + e.getMessage());
        }
    }

    public List<Reglefiltre> searchRegles(String keyword) {
        List<Reglefiltre> list = new ArrayList<>();
        String sql = "SELECT * FROM reglefiltre WHERE ip LIKE ? OR protocole LIKE ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Reglefiltre(
                            rs.getInt("id_filtre"),
                            rs.getString("ip"),
                            rs.getString("protocole"),
                            rs.getString("action")));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur recherche règles : " + e.getMessage());
        }
        return list;
    }
}

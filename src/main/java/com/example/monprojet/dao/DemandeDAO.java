package com.example.monprojet.dao;

import com.example.monprojet.Util.DBConnection;
import com.example.monprojet.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DemandeDAO {

    // ➕ Ajouter une demande
    public void ajouterDemande(Demande d) {
        String sql = "INSERT INTO demandes (duree, date_heure, utilisateur_id, poste_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, d.getDuree());
            stmt.setTimestamp(2, Timestamp.valueOf(d.getDateHeure()));
            stmt.setInt(3, d.getUtilisateur().getId()); // ⚠️ assure-toi que Utilisateur a bien getId()
            stmt.setInt(4, d.getPoste().getId()); // ⚠️ idem pour Poste

            stmt.executeUpdate();
            System.out.println("✅ Demande ajoutée !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur ajout demande : " + e.getMessage());
        }
    }

    // 📖 Lire toutes les demandes
    public List<Demande> getAllDemandes() {
        List<Demande> liste = new ArrayList<>();
        String sql = "SELECT d.id, d.duree, d.date_heure, " +
                "u.id AS uid, u.nom, u.prenom, u.password, u.date_abonne, " +
                "p.id AS pid, p.reference, p.adresse_ip, p.emplacement, p.etat " +
                "FROM demandes d " +
                "JOIN utilisateurs u ON d.utilisateur_id = u.id " +
                "JOIN postes p ON d.poste_id = p.id";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Utilisateur u = new Utilisateur(
                        rs.getInt("uid"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("password"),
                        rs.getDate("date_abonne").toLocalDate()

                );

                Poste p = new Poste(
                        rs.getInt("pid"),
                        rs.getString("reference"),
                        rs.getString("adresse_ip"),
                        rs.getString("emplacement"),
                        rs.getString("etat"));

                Demande d = new Demande(
                        rs.getInt("id"),
                        rs.getInt("duree"),
                        rs.getTimestamp("date_heure").toLocalDateTime(),
                        u,
                        p);

                liste.add(d);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lecture demandes : " + e.getMessage());
        }
        return liste;
    }

    // ✏️ Modifier une demande
    public void modifierDemande(Demande d) {
        String sql = "UPDATE demandes SET duree=?, date_heure=?, utilisateur_id=?, poste_id=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, d.getDuree());
            stmt.setTimestamp(2, Timestamp.valueOf(d.getDateHeure()));
            stmt.setInt(3, d.getUtilisateur().getId());
            stmt.setInt(4, d.getPoste().getId());
            stmt.setInt(5, d.getId());

            System.out.println("DEBUG ➜ UPDATE Demande ID = " + d.getId());
            int rows = stmt.executeUpdate(); // une seule exécution
            System.out.println("DEBUG ➜ Lignes modifiées = " + rows);

            if (rows > 0) {
                System.out.println("✅ Demande modifiée !");
            } else {
                System.out.println("⚠️ Aucune ligne mise à jour (ID introuvable ?)");
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur modification demande : " + e.getMessage());
        }
    }

    // ❌ Supprimer une demande
    public void supprimerDemande(int id) {
        String sql = "DELETE FROM demandes WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✅ Demande supprimée !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur suppression demande : " + e.getMessage());
        }
    }

    // 🔍 RECHERCHE par date (jour entier)
    public List<Demande> searchByDate(LocalDate date) {
        List<Demande> liste = new ArrayList<>();
        String sql = "SELECT d.id, d.duree, d.date_heure, " +
                "u.id AS uid, u.nom, u.prenom, u.password, u.date_abonne, " +
                "p.id AS pid, p.reference, p.adresse_ip, p.emplacement, p.etat " +
                "FROM demandes d " +
                "JOIN utilisateurs u ON d.utilisateur_id = u.id " +
                "JOIN postes p ON d.poste_id = p.id " +
                "WHERE DATE(d.date_heure) = ?"; // 👈 filtre uniquement la date

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Utilisateur u = new Utilisateur(
                            rs.getInt("uid"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("password"),
                            rs.getDate("date_abonne").toLocalDate());

                    Poste p = new Poste(
                            rs.getInt("pid"),
                            rs.getString("reference"),
                            rs.getString("adresse_ip"),
                            rs.getString("emplacement"),
                            rs.getString("etat"));

                    Demande d = new Demande(
                            rs.getInt("id"),
                            rs.getInt("duree"),
                            rs.getTimestamp("date_heure").toLocalDateTime(),
                            u,
                            p);
                    liste.add(d);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur recherche demande par date : " + e.getMessage());
        }
        return liste;
    }

}

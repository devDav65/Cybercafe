package com.example.monprojet.dao;

import com.example.monprojet.Util.DBConnection;
import com.example.monprojet.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    // ➕ CREATE : ajouter une réservation
    public void ajouterReservation(Reservation r) {
        String sql = "INSERT INTO reservations (date_reservation, heure, utilisateur_id, poste_id) VALUES (?, ?, ?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(r.getDateReservation()));
            stmt.setTime(2, Time.valueOf(r.getHeure()));
            stmt.setInt(3, r.getUtilisateur().getId()); // ⚠️ Utilisateur doit avoir son ID
            stmt.setInt(4, r.getPoste().getId()); // ⚠️ Poste doit avoir son ID

            stmt.executeUpdate();

            // 🔑 Récupération de l’ID auto-généré
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setId(rs.getInt(1));
                }
            }

            System.out.println("✅ Réservation ajoutée !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur ajout réservation : " + e.getMessage());
        }
    }

    // 📖 READ : récupérer toutes les réservations
    public List<Reservation> getAllReservations() {
        List<Reservation> liste = new ArrayList<>();
        String sql = "SELECT r.id, r.date_reservation, r.heure, " +
                "u.id AS uid, u.nom, u.prenom, u.password, u.date_abonne, " +
                "p.id AS pid, p.reference, p.adresse_ip, p.emplacement, p.etat " +
                "FROM reservations r " +
                "JOIN utilisateurs u ON r.utilisateur_id = u.id " +
                "JOIN postes p ON r.poste_id = p.id";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

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

                Reservation r = new Reservation(
                        rs.getInt("id"),
                        rs.getDate("date_reservation").toLocalDate(),
                        rs.getTime("heure").toLocalTime(),
                        u,
                        p);
                liste.add(r);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lecture réservations : " + e.getMessage());
        }
        return liste;
    }

    // 📖 READ : récupérer une réservation par ID
    public Reservation getReservationById(int id) {
        String sql = "SELECT r.id, r.date_reservation, r.heure, " +
                "u.id AS uid, u.nom, u.prenom, u.password, u.date_abonne, " +
                "p.id AS pid, p.reference, p.adresse_ip, p.emplacement, p.etat " +
                "FROM reservations r " +
                "JOIN utilisateurs u ON r.utilisateur_id = u.id " +
                "JOIN postes p ON r.poste_id = p.id " +
                "WHERE r.id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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

                    return new Reservation(
                            rs.getInt("id"),
                            rs.getDate("date_reservation").toLocalDate(),
                            rs.getTime("heure").toLocalTime(),
                            u,
                            p);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lecture réservation par ID : " + e.getMessage());
        }
        return null;
    }

    // ✏️ UPDATE : modifier une réservation
    public void updateReservation(Reservation r) {
        String sql = "UPDATE reservations SET date_reservation = ?, heure = ?, utilisateur_id = ?, poste_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(r.getDateReservation()));
            stmt.setTime(2, Time.valueOf(r.getHeure()));
            stmt.setInt(3, r.getUtilisateur().getId());
            stmt.setInt(4, r.getPoste().getId());
            stmt.setInt(5, r.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Réservation mise à jour !");
            } else {
                System.out.println("⚠️ Aucune réservation trouvée avec l’ID " + r.getId());
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur mise à jour réservation : " + e.getMessage());
        }
    }

    // 🗑️ DELETE : supprimer une réservation
    public void deleteReservation(int id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Réservation supprimée !");
            } else {
                System.out.println("⚠️ Aucune réservation trouvée avec l’ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur suppression réservation : " + e.getMessage());
        }
    }

    // 🔍 RECHERCHE par date
    public List<Reservation> searchByDate(LocalDate date) {
        List<Reservation> liste = new ArrayList<>();
        String sql = "SELECT r.id, r.date_reservation, r.heure, " +
                "u.id AS uid, u.nom, u.prenom, u.password, u.date_abonne, " +
                "p.id AS pid, p.reference, p.adresse_ip, p.emplacement, p.etat " +
                "FROM reservations r " +
                "JOIN utilisateurs u ON r.utilisateur_id = u.id " +
                "JOIN postes p ON r.poste_id = p.id " +
                "WHERE r.date_reservation = ?";

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

                    Reservation r = new Reservation(
                            rs.getInt("id"),
                            rs.getDate("date_reservation").toLocalDate(),
                            rs.getTime("heure").toLocalTime(),
                            u,
                            p);
                    liste.add(r);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur recherche réservation par date : " + e.getMessage());
        }
        return liste;
    }

}

package com.example.monprojet.dao;

import com.example.monprojet.Util.DBConnection;
import com.example.monprojet.Util.Logger;
import com.example.monprojet.model.Journal;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class JournalDAO {

    public void ajouterJournal(Journal j) {
        String sql = "INSERT INTO journal (ipsource, ipdestination, protocole, horlotage, action) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, j.getIpsource());
            ps.setString(2, j.getIpdestination());
            ps.setString(3, j.getProtocole());

            LocalTime ht = j.getHorlotage();
            if (ht != null) {
                ps.setTime(4, Time.valueOf(ht));
            } else {
                ps.setNull(4, Types.TIME);
            }

            ps.setString(5, j.getAction());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                Logger.logInfo("Aucune ligne insérée dans journal.");
            } else {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        j.setId_journal(rs.getInt(1));
                    }
                }
                // Logger.logInfo("✅ Journal ajouté : " + j.getIpsource() + " -> " +
                // j.getIpdestination());
            }
        } catch (SQLException e) {
            Logger.logError("❌ Erreur ajout journal", e);
        }
    }

    public List<Journal> getAllJournaux() {
        List<Journal> list = new ArrayList<>();
        String sql = "SELECT id_journal, ipsource, ipdestination, protocole, horlotage, action FROM journal ORDER BY id_journal DESC";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Journal j = mapResultSetToJournal(rs);
                list.add(j);
            }
        } catch (SQLException e) {
            Logger.logError("❌ Erreur lecture journaux", e);
        }
        return list;
    }

    public Journal getJournalById(int id) {
        String sql = "SELECT id_journal, ipsource, ipdestination, protocole, horlotage, action FROM journal WHERE id_journal = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToJournal(rs);
                }
            }
        } catch (SQLException e) {
            Logger.logError("❌ Erreur lecture journal par id", e);
        }
        return null;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM journal WHERE id_journal = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                Logger.logInfo("✅ Journal supprimé id=" + id);
                return true;
            } else {
                Logger.logInfo("⚠️ Aucun journal trouvé pour id=" + id);
                return false;
            }
        } catch (SQLException e) {
            Logger.logError("❌ Erreur suppression journal", e);
            return false;
        }
    }

    public List<Journal> searchByIp(String keyword) {
        List<Journal> list = new ArrayList<>();
        String sql = "SELECT id_journal, ipsource, ipdestination, protocole, horlotage, action FROM journal WHERE ipsource LIKE ? OR ipdestination LIKE ? ORDER BY id_journal DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToJournal(rs));
                }
            }
        } catch (SQLException e) {
            Logger.logError("❌ Erreur recherche journal par IP", e);
        }
        return list;
    }

    public int deleteOlderThanDays(int days) {

        Logger.logInfo("deleteOlderThanDays non exécutée : vérifie la présence d'une colonne date si besoin.");
        return 0;
    }

    private Journal mapResultSetToJournal(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_journal");
        String src = rs.getString("ipsource");
        String dst = rs.getString("ipdestination");
        String proto = rs.getString("protocole");

        Time t = rs.getTime("horlotage");
        LocalTime horloge = t != null ? t.toLocalTime() : null;

        String action = rs.getString("action");

        return new Journal(id, src, dst, proto, horloge, action);
    }
}

package com.example.monprojet.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.monprojet.dao.ReservationDAO;
import com.example.monprojet.dao.UtilisateurDAO;
import com.example.monprojet.dao.PosteDAO;
import com.example.monprojet.model.Reservation;
import com.example.monprojet.model.Utilisateur;
import com.example.monprojet.model.Poste;

public class ReservationController {
    @FXML
    private DatePicker searchDatePicker;
    @FXML
    private DatePicker dateField;
    @FXML
    private TextField heureField;
    @FXML
    private TextField utilisateurIdField;
    @FXML
    private TextField posteIdField;
    @FXML
    private TableView<Reservation> reservationTable;
    @FXML
    private TableColumn<Reservation, Integer> colId;
    @FXML
    private TableColumn<Reservation, LocalDate> colDate;
    @FXML
    private TableColumn<Reservation, LocalTime> colHeure;
    @FXML
    private TableColumn<Reservation, String> colUtilisateur;
    @FXML
    private TableColumn<Reservation, String> colPoste;

    private ReservationDAO reservationDAO = new ReservationDAO();
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private PosteDAO posteDAO = new PosteDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
        colHeure.setCellValueFactory(new PropertyValueFactory<>("heure"));
        colUtilisateur.setCellValueFactory(cellData -> {
            Utilisateur u = cellData.getValue().getUtilisateur();
            return new javafx.beans.property.SimpleStringProperty(u != null ? u.getNom() + " " + u.getPrenom() : "");
        });
        colPoste.setCellValueFactory(cellData -> {
            Poste p = cellData.getValue().getPoste();
            return new javafx.beans.property.SimpleStringProperty(p != null ? p.getReference() : "");
        });

        loadReservations();
    }

    private void loadReservations() {
        List<Reservation> list = reservationDAO.getAllReservations();
        ObservableList<Reservation> obs = FXCollections.observableArrayList(list);
        reservationTable.setItems(obs);
    }

    @FXML
    private void ajouterReservation() {
        // ✅ Vérification des champs avant parsing
        if (dateField.getValue() == null || heureField.getText().trim().isEmpty()
                || utilisateurIdField.getText().trim().isEmpty() || posteIdField.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Tous les champs doivent être remplis !").show();
            return;
        }

        try {
            LocalDate date = dateField.getValue();
            LocalTime heure = LocalTime.parse(heureField.getText().trim());
            int userId = Integer.parseInt(utilisateurIdField.getText().trim());
            int posteId = Integer.parseInt(posteIdField.getText().trim());

            // recherche utilisateur et poste
            Utilisateur u = utilisateurDAO.getUtilisateurById(userId);
            Poste p = posteDAO.getPosteById(posteId);

            if (u == null || p == null) {
                new Alert(Alert.AlertType.WARNING, "Utilisateur ou Poste invalide !").show();
                return;
            }

            Reservation r = new Reservation(0, date, heure, u, p);
            reservationDAO.ajouterReservation(r);
            loadReservations();

            dateField.setValue(null);
            heureField.clear();
            utilisateurIdField.clear();
            posteIdField.clear();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Les champs ID doivent être des nombres !").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout : " + e.getMessage()).show();
        }
    }

    @FXML
    private void rechercherParDate() {
        LocalDate d = searchDatePicker.getValue();
        if (d == null) {
            new Alert(Alert.AlertType.INFORMATION, "Choisissez une date").show();
            return;
        }
        List<Reservation> res = reservationDAO.searchByDate(d);
        reservationTable.setItems(FXCollections.observableArrayList(res));
    }

    @FXML
    private void resetRecherche() {
        searchDatePicker.setValue(null);
        loadReservations();
    }

    // ✏️ Modifier la réservation sélectionnée (exemple : juste la date/heure)
    @FXML
    private void modifierReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez une réservation à modifier").show();
            return;
        }
        // Ici tu peux ouvrir une boîte de dialogue pour éditer date/heure etc.
        // Ex : selected.setDateReservation(nouvelleDate); ...
        reservationDAO.updateReservation(selected);
        loadReservations();
    }

    // 🗑️ Supprimer
    @FXML
    private void supprimerReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez une réservation à modifier").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer cette réservation ?");
        confirm.setContentText("ID : " + selected.getId() + "\nUtilisateur : " +
                selected.getUtilisateur().getNom());

        ButtonType ok = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(ok, cancel);

        confirm.showAndWait().ifPresent(type -> {
            if (type == ok) {
                reservationDAO.deleteReservation(selected.getId());
                loadReservations();
                new Alert(Alert.AlertType.CONFIRMATION, "Reservation supprimé avec succès !!").show();
                ;
            }
        });
    }

}

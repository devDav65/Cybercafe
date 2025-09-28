package com.example.monprojet.controllers;

import com.example.monprojet.dao.JournalDAO;
import com.example.monprojet.model.Journal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Contrôleur JavaFX pour l'affichage du journal réseau
 */
public class JournalController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Journal> journalTable;
    @FXML
    private TableColumn<Journal, Integer> colId;
    @FXML
    private TableColumn<Journal, String> colSource;
    @FXML
    private TableColumn<Journal, String> colDest;
    @FXML
    private TableColumn<Journal, String> colProto;
    @FXML
    private TableColumn<Journal, LocalTime> colHeure;
    @FXML
    private TableColumn<Journal, String> colAction;

    private final JournalDAO journalDAO = new JournalDAO();
    private final ObservableList<Journal> data = FXCollections.observableArrayList();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @FXML
    public void initialize() {
        // lie les colonnes aux propriétés du modèle
        colId.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId_journal()).asObject());
        colSource.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getIpsource()));
        colDest.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getIpdestination()));
        colProto.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getProtocole()));
        colHeure.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getHorlotage()));
        colAction.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getAction()));

        chargerJournaux();
        startAutoRefresh();
    }

    private void chargerJournaux() {
        List<Journal> liste = journalDAO.getAllJournaux();
        data.setAll(liste);
        journalTable.setItems(data);
    }

    private void startAutoRefresh() {
        scheduler.scheduleAtFixedRate(() -> {
            List<Journal> list = journalDAO.getAllJournaux(); // Requêter la DB
            Platform.runLater(() -> journalTable.setItems(FXCollections.observableArrayList(list)));
        }, 0, 2, TimeUnit.SECONDS);
    }

    /** Recherche par mot-clé (IP source ou destination) */
    @FXML
    private void rechercher() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) {
            showAlert("Entrez une IP ou un mot-clé pour rechercher.");
            return;
        }
        List<Journal> results = journalDAO.searchByIp(kw);
        data.setAll(results);
    }

    /** Réinitialise la recherche */
    @FXML
    private void reset() {
        searchField.clear();
        chargerJournaux();
    }

    /** Supprime la ligne sélectionnée */
    @FXML
    private void supprimerSelection() {
        Journal selected = journalTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélectionnez une entrée à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer l'entrée ID " + selected.getId_journal() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                boolean ok = journalDAO.delete(selected.getId_journal());
                if (ok) {
                    data.remove(selected);
                } else {
                    showAlert("Impossible de supprimer l'entrée.");
                }
            }
        });
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }

    @Override
    public void finalize() throws Throwable {
        scheduler.shutdownNow();
    }
}

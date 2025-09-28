package com.example.monprojet.controllers;

import com.example.monprojet.model.Journal;
import com.example.monprojet.service.JournalService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private final JournalService journalService = new JournalService();
    private final ObservableList<Journal> data = FXCollections.observableArrayList();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @FXML
    public void initialize() {
        // Lier les colonnes aux propriétés du modèle
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

        // Charger les journaux existants
        chargerJournaux();

        // Démarrer le rafraîchissement automatique toutes les 2 secondes
        startAutoRefresh();
    }

    /** Charge tous les journaux depuis la DB */
    private void chargerJournaux() {
        List<Journal> liste = journalService.listerJournaux();
        data.setAll(liste);
        journalTable.setItems(data);
    }

    /** Rafraîchissement automatique en temps réel */
    private void startAutoRefresh() {
        scheduler.scheduleAtFixedRate(() -> {
            List<Journal> list = journalService.listerJournaux();
            Platform.runLater(() -> {
                data.setAll(list);
                journalTable.setItems(data);
            });
        }, 0, 2, TimeUnit.SECONDS);
    }

    /** Recherche par IP source, destination ou protocole */
    @FXML
    private void rechercher() {
        String kw = searchField.getText().trim().toLowerCase();
        if (kw.isEmpty()) {
            showAlert("Entrez une IP ou un protocole pour rechercher.");
            return;
        }

        List<Journal> filtered = journalService.listerJournaux().stream()
                .filter(j -> j.getIpsource().toLowerCase().contains(kw) ||
                        j.getIpdestination().toLowerCase().contains(kw) ||
                        j.getProtocole().toLowerCase().contains(kw))
                .toList();

        data.setAll(filtered);
    }

    /** Réinitialise la recherche */
    @FXML
    private void reset() {
        searchField.clear();
        chargerJournaux();
    }

    /** Supprime l’entrée sélectionnée */
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
                boolean ok = journalService.supprimerJournal(selected.getId_journal());
                if (ok) {
                    data.remove(selected);
                } else {
                    showAlert("Impossible de supprimer l'entrée.");
                }
            }
        });
    }

    /** Affiche une alerte simple */
    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }

    /** Arrêt du scheduler à la fermeture */
    public void stop() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow();
            System.out.println("JournalController scheduler stopped.");
        }

    }
}

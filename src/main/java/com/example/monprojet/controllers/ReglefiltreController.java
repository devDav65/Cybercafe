package com.example.monprojet.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

import com.example.monprojet.dao.ReglefiltreDAO;
import com.example.monprojet.model.Reglefiltre;

public class ReglefiltreController {

    @FXML
    private TextField ipField;
    @FXML
    private ComboBox<String> protocoleCombo;
    @FXML
    private ComboBox<String> actionCombo;
    @FXML
    private TextField searchField;

    @FXML
    private TableView<Reglefiltre> tableRegles;
    @FXML
    private TableColumn<Reglefiltre, Integer> colId;
    @FXML
    private TableColumn<Reglefiltre, String> colIp;
    @FXML
    private TableColumn<Reglefiltre, String> colProtocole;
    @FXML
    private TableColumn<Reglefiltre, String> colAction;

    private final ReglefiltreDAO regleDAO = new ReglefiltreDAO();

    @FXML
    public void initialize() {
        protocoleCombo.setItems(FXCollections.observableArrayList("TCP", "UDP", "ICMP", "ANY"));
        actionCombo.setItems(FXCollections.observableArrayList("block", "allow"));

        colId.setCellValueFactory(new PropertyValueFactory<>("id_filtre"));
        colIp.setCellValueFactory(new PropertyValueFactory<>("ip"));
        colProtocole.setCellValueFactory(new PropertyValueFactory<>("protocole"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));

        loadRegles();
    }

    private void loadRegles() {
        List<Reglefiltre> list = regleDAO.getAllRegles();
        tableRegles.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void ajouterRegle() {
        String ip = ipField.getText().trim();
        String proto = protocoleCombo.getValue();
        String action = actionCombo.getValue();

        if (proto == null || action == null) {
            showAlert("Choisissez un protocole et une action");
            return;
        }

        regleDAO.ajouterRegle(new Reglefiltre(0, ip, proto, action));
        clearFields();
        loadRegles();
    }

    @FXML
    private void modifierRegle() {
        Reglefiltre selected = tableRegles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélectionnez une règle à modifier");
            return;
        }

        selected.setIP(ipField.getText().trim());
        selected.setProtocole(protocoleCombo.getValue());
        selected.setAction(actionCombo.getValue());

        regleDAO.updateRegle(selected);
        clearFields();
        loadRegles();
    }

    @FXML
    private void supprimerRegle() {
        Reglefiltre selected = tableRegles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélectionnez une règle à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer cette règle ?");
        confirm.setContentText("IP : " + selected.getIp() +
                "\nProtocole : " + selected.getProtocole() +
                "\nAction : " + selected.getAction());

        ButtonType ok = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(ok, cancel);

        confirm.showAndWait().ifPresent(type -> {
            if (type == ok) {
                regleDAO.deleteRegle(selected.getId_filtre());
                clearFields();
                loadRegles();
                showAlert("Règle supprimée avec succès !");
            }
        });
    }

    @FXML
    private void rechercherRegle() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) {
            showAlert("Entrez un mot clé (IP ou protocole)");
            return;
        }
        List<Reglefiltre> list = regleDAO.searchRegles(kw);
        tableRegles.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void resetRecherche() {
        searchField.clear();
        loadRegles();
    }

    private void clearFields() {
        ipField.clear();
        protocoleCombo.getSelectionModel().clearSelection();
        actionCombo.getSelectionModel().clearSelection();
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }
}

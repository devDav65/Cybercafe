package com.example.monprojet.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

import com.example.monprojet.dao.PosteDAO;
import com.example.monprojet.model.Poste;

public class PosteController {
    @FXML
    private TextField searchField;
    @FXML
    private TextField referenceField;
    @FXML
    private TextField ipField;
    @FXML
    private TextField emplacementField;
    @FXML
    private ComboBox<String> etatCombo;
    @FXML
    private TableView<Poste> posteTable;
    @FXML
    private TableColumn<Poste, Integer> colId;
    @FXML
    private TableColumn<Poste, String> colReference;
    @FXML
    private TableColumn<Poste, String> colIP;
    @FXML
    private TableColumn<Poste, String> colEmplacement;
    @FXML
    private TableColumn<Poste, String> colEtat;

    private PosteDAO posteDAO = new PosteDAO();

    @FXML
    public void initialize() {
        etatCombo.setItems(FXCollections.observableArrayList("disponible", "occupe", "maintenance"));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colIP.setCellValueFactory(new PropertyValueFactory<>("adresseIp")); // adapte si ton getter est getIP()
        colEmplacement.setCellValueFactory(new PropertyValueFactory<>("emplacement"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        loadPostes();
    }

    private void loadPostes() {
        List<Poste> list = posteDAO.getAllPostes();
        ObservableList<Poste> obs = FXCollections.observableArrayList(list);
        posteTable.setItems(obs);
    }

    @FXML
    private void ajouterPoste() {
        String ref = referenceField.getText().trim();
        String ip = ipField.getText().trim();
        String emplacement = emplacementField.getText().trim();
        String etat = etatCombo.getValue();

        if (ref.isEmpty() || ip.isEmpty() || etat == null) {
            new Alert(Alert.AlertType.WARNING, "Référence, IP et état requis.").show();
            return;
        }

        Poste p = new Poste(0, ref, ip, emplacement, etat); // adapte au constructeur
        posteDAO.ajouterPoste(p);
        referenceField.clear();
        ipField.clear();
        emplacementField.clear();
        etatCombo.getSelectionModel().clearSelection();
        loadPostes();
    }

    @FXML
    private void modifierPoste() {
        Poste selected = posteTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélectionnez un poste à modifier");
            return;
        }
        selected.setReference(referenceField.getText().trim());
        selected.setAdresseIp(ipField.getText().trim());
        selected.setEmplacement(emplacementField.getText().trim());
        selected.setEtat(etatCombo.getValue());

        posteDAO.updatePoste(selected);
        clearFields();
        loadPostes();

    }

    @FXML
    private void supprimerPoste() {
        Poste selected = posteTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélectionnez un poste à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer ce poste ?");
        confirm.setContentText("Référence : " + selected.getReference() + "\nIP : " + selected.getAdresseIp());

        ButtonType ok = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(ok, cancel);

        confirm.showAndWait().ifPresent(type -> {
            if (type == ok) {
                posteDAO.deletePoste(selected.getId());
                clearFields();
                loadPostes();
                showAlert("Poste supprimé avec succès !");
            }
        });
    }

    @FXML
    private void rechercherPoste() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) {
            showAlert("Entrez une référence ou une IP à rechercher.");
            return;
        }
        List<Poste> res = posteDAO.searchPostes(kw);
        posteTable.setItems(FXCollections.observableArrayList(res));
    }

    @FXML
    private void resetRecherche() {
        searchField.clear();
        loadPostes();
    }

    private void clearFields() {
        referenceField.clear();
        ipField.clear();
        emplacementField.clear();
        // etatCombo.clear();
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }

}

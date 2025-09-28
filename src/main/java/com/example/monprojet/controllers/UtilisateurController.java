package com.example.monprojet.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.List;

import com.example.monprojet.dao.UtilisateurDAO;
import com.example.monprojet.model.Utilisateur;
import com.example.monprojet.Util.PasswordUtils;
import com.example.monprojet.Util.Validator;

public class UtilisateurController {
    @FXML
    private TextField searchField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TableView<Utilisateur> utilisateurTable;
    @FXML
    private TableColumn<Utilisateur, Integer> colId;
    @FXML
    private TableColumn<Utilisateur, String> colNom;
    @FXML
    private TableColumn<Utilisateur, String> colPrenom;
    @FXML
    private TableColumn<Utilisateur, LocalDate> colDateAbonne;

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colDateAbonne.setCellValueFactory(new PropertyValueFactory<>("dateAbonne"));

        loadUtilisateurs();
    }

    private void loadUtilisateurs() {
        List<Utilisateur> list = utilisateurDAO.getUtilisateurs();
        ObservableList<Utilisateur> obs = FXCollections.observableArrayList(list);
        utilisateurTable.setItems(obs);
    }

    @FXML
    private void ajouterUtilisateur() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String password = passwordField.getText();

        if (!Validator.isNotEmpty(nom) || !Validator.isNotEmpty(prenom) || !Validator.isValidPassword(password)) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Nom, prénom et mot de passe (>=6) requis.");
            a.show();
            return;
        }

        String hashed = PasswordUtils.hashPassword(password);
        Utilisateur u = new Utilisateur(0, nom, prenom, hashed, LocalDate.now()); // exemple
        utilisateurDAO.ajouterUtilisateur(u);

        nomField.clear();
        prenomField.clear();
        passwordField.clear();
        loadUtilisateurs();
    }

    @FXML
    private void modifierUtilisateur() {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélectionnez un utilisateur à modifier.");
            return;
        }

        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String password = passwordField.getText();

        if (!Validator.isNotEmpty(nom) || !Validator.isNotEmpty(prenom)) {
            showAlert("Nom et prénom requis.");
            return;
        }

        selected.setNom(nom);
        selected.setPrenom(prenom);
        if (Validator.isNotEmpty(password)) {
            selected.setPassword(PasswordUtils.hashPassword(password));
        }

        utilisateurDAO.updateUtilisateur(selected);
        loadUtilisateurs();
        clearFields();
    }

    @FXML
    private void rechercherUtilisateur() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            showAlert("Veuillez saisir un nom ou un prénom.");
            return;
        }
        List<Utilisateur> result = utilisateurDAO.searchUtilisateurs(keyword);
        utilisateurTable.setItems(FXCollections.observableArrayList(result));
    }

    @FXML
    private void resetRecherche() {
        searchField.clear();
        loadUtilisateurs(); // recharge toute la liste
    }

    @FXML
    private void supprimerUtilisateur() {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélectionnez un utilisateur à supprimer.");
            return;
        }

        // 🔔 Créer une boîte de confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer cet utilisateur ?");
        confirm.setContentText("Nom : " + selected.getNom() + " " + selected.getPrenom());

        // Personnaliser les boutons
        ButtonType ok = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(ok, cancel);

        // Attendre la réponse de l’utilisateur
        confirm.showAndWait().ifPresent(type -> {
            if (type == ok) {
                // ✅ Suppression seulement si confirmé
                utilisateurDAO.deleteUtilisateur(selected.getId());
                loadUtilisateurs();
                clearFields();
                showAlert("Utilisateur supprimé avec succès !");
            }
            // sinon, rien n’est fait
        });
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        passwordField.clear();
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).show();
    }

}

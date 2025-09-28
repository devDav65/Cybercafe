package com.example.monprojet.controllers;

import com.example.monprojet.model.Proprietaire;
import com.example.monprojet.service.ProprietaireService;
import com.example.monprojet.Util.PasswordUtils;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SignupController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField pinField;

    private ProprietaireService proprietaireService = new ProprietaireService();

    @FXML
    private void handleSignup() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String pin = pinField.getText();

        if (username.isEmpty() || password.isEmpty() || pin.isEmpty()) {
            showAlert(AlertType.ERROR, "Tous les champs sont obligatoires");
            return;
        }

        String hash = PasswordUtils.hashPassword(password);
        Proprietaire p = new Proprietaire(0, username, hash, pin);

        if (proprietaireService.enregistrer(p)) {
            showAlert(AlertType.INFORMATION, "Compte créé avec succès !");
            usernameField.clear();
            passwordField.clear();
            pinField.clear();
        } else {
            showAlert(AlertType.ERROR, "Échec de l'enregistrement. Vérifiez le nom d'utilisateur.");
        }
    }

    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

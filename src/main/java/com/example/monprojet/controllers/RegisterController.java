package com.example.monprojet.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

import com.example.monprojet.dao.ProprietaireDAO;
import com.example.monprojet.model.Proprietaire;
import com.example.monprojet.Util.PasswordUtils;
import com.example.monprojet.Util.Validator;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private PasswordField pinField;
    @FXML
    private PasswordField confirmPinField;
    @FXML
    private Label messageLabel;

    private ProprietaireDAO proprietaireDAO = new ProprietaireDAO();

    @FXML
    public void initialize() {
        messageLabel.setText("");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String pin = pinField.getText();
        String confirmPin = confirmPinField.getText();

        // --- validations de base ---
        if (!Validator.isNotEmpty(username) || !Validator.isNotEmpty(password)
                || !Validator.isNotEmpty(pin)) {
            messageLabel.setText("Tous les champs sont requis.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }
        if (!pin.equals(confirmPin)) {
            messageLabel.setText("Les codes PIN ne correspondent pas.");
            return;
        }
        if (!Validator.isValidPassword(password)) {
            messageLabel.setText("Mot de passe trop court (≥6 caractères).");
            return;
        }
        if (!Validator.isValidPassword(pin) || pin.length() < 4) {
            messageLabel.setText("PIN doit être uniquement numérique (≥4 chiffres).");
            return;
        }

        // --- vérifie si username déjà pris ---
        if (proprietaireDAO.getByUsername(username) != null) {
            messageLabel.setText("Nom d'utilisateur déjà utilisé.");
            return;
        }

        // --- hash et enregistrement ---
        String hashedPass = PasswordUtils.hashPassword(password);
        String hashedPin = PasswordUtils.hashPassword(pin);

        Proprietaire p = new Proprietaire(username, confirmPassword, pin);
        p.setUsername(username);
        p.setPassword(hashedPass);
        p.setPinCode(hashedPin);

        proprietaireDAO.ajouterProprietaire(p);

        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Compte créé ! Vous pouvez vous connecter.");
    }

    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        Parent login = FXMLLoader.load(getClass().getResource("/View/LoginView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(login);
    }
}

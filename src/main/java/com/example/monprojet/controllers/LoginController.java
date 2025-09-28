package com.example.monprojet.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

import com.example.monprojet.dao.ProprietaireDAO;
import com.example.monprojet.model.Proprietaire;
import com.example.monprojet.Util.PasswordUtils;
import com.example.monprojet.Util.Validator;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField pinField;
    @FXML
    private Label errorLabel;

    private ProprietaireDAO proprietaireDAO = new ProprietaireDAO();

    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

    // appelé par onAction="#handleLogin" dans LoginView.fxml
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String pin = pinField.getText();

        if (!Validator.isNotEmpty(username) || !Validator.isNotEmpty(password) || !Validator.isNotEmpty(pin)) {
            errorLabel.setText("Remplissez tous les champs.");
            return;
        }

        Proprietaire p = proprietaireDAO.getByUsername(username);
        if (p == null) {
            errorLabel.setText("Propriétaire introuvable.");
            return;
        }

        boolean passOk = PasswordUtils.checkPassword(password, p.getPassword());
        boolean pinOk = pin.equals(p.getPinCode()); // ✅ comparaison simple

        if (passOk && pinOk) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/DashboardView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                errorLabel.setText("Erreur de chargement du Dashboard");
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Identifiants ou code PIN incorrects.");
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) throws IOException {
        Parent register = FXMLLoader.load(getClass().getResource("/View/RegisterView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(register);
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/View/SignupView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Créer un compte");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

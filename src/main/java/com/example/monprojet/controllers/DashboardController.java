package com.example.monprojet.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
//import java.nio.file.Paths;

//import com.example.monprojet.service.PacketProcessor;

public class DashboardController {

    @FXML
    private StackPane mainContent;

    @FXML
    public void initialize() {

    }

    private void setContent(String fxmlPath) {
        try {
            Parent node = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContent.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showUtilisateurs(ActionEvent e) {
        setContent("/View/UtilisateurView.fxml");
    }

    @FXML
    private void showPostes(ActionEvent e) {
        setContent("/View/PosteView.fxml");
    }

    @FXML
    private void showReservations(ActionEvent e) {
        setContent("/View/ReservationView.fxml");
    }

    @FXML
    private void showDemandes(ActionEvent e) {
        setContent("/View/DemandeView.fxml");
    }

    @FXML
    private void showJournaux(ActionEvent e) {
        setContent("/View/JournalView.fxml");
    }

    @FXML
    private void showRegles(ActionEvent e) {
        setContent("/View/ReglefiltreView.fxml");
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        Parent login = FXMLLoader.load(getClass().getResource("/View/LoginView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(login);
    }
}

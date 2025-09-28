package com.example.monprojet.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.monprojet.dao.DemandeDAO;
import com.example.monprojet.dao.UtilisateurDAO;
import com.example.monprojet.dao.PosteDAO;
import com.example.monprojet.model.Demande;
import com.example.monprojet.model.Utilisateur;
import com.example.monprojet.model.Poste;

public class DemandeController {
    @FXML
    private DatePicker searchDatePicker;
    @FXML
    private TextField dureeField;
    @FXML
    private DatePicker dateField;
    @FXML
    private TextField heureField;
    @FXML
    private TextField utilisateurIdField;
    @FXML
    private TextField posteIdField;
    @FXML
    private TableView<Demande> demandeTable;
    @FXML
    private TableColumn<Demande, Integer> colId;
    @FXML
    private TableColumn<Demande, Integer> colDuree;
    @FXML
    private TableColumn<Demande, LocalDateTime> colDateHeure;
    @FXML
    private TableColumn<Demande, String> colUtilisateur;
    @FXML
    private TableColumn<Demande, String> colPoste;

    private DemandeDAO demandeDAO = new DemandeDAO();
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private PosteDAO posteDAO = new PosteDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("duree"));
        colDateHeure.setCellValueFactory(new PropertyValueFactory<>("dateHeure"));
        colUtilisateur.setCellValueFactory(cellData -> {
            Utilisateur u = cellData.getValue().getUtilisateur();
            return new javafx.beans.property.SimpleStringProperty(u != null ? u.getNom() + " " + u.getPrenom() : "");
        });
        colPoste.setCellValueFactory(cellData -> {
            Poste p = cellData.getValue().getPoste();
            return new javafx.beans.property.SimpleStringProperty(p != null ? p.getReference() : "");
        });

        loadDemandes();
    }

    private void loadDemandes() {
        List<Demande> list = demandeDAO.getAllDemandes();
        demandeTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void ajouterDemande() {
        // Vérification des champs
        if (dureeField.getText().isEmpty() || utilisateurIdField.getText().isEmpty() ||
                posteIdField.getText().isEmpty() || dateField.getValue() == null || heureField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Tous les champs doivent être remplis !").show();
            return;
        }

        try {
            int duree = Integer.parseInt(dureeField.getText().trim());
            int userId = Integer.parseInt(utilisateurIdField.getText().trim());
            int posteId = Integer.parseInt(posteIdField.getText().trim());
            LocalDate date = dateField.getValue();
            LocalTime time = LocalTime.parse(heureField.getText().trim());

            Utilisateur u = utilisateurDAO.getUtilisateurById(userId);
            Poste p = posteDAO.getPosteById(posteId);
            p.setEtat("occupe");
            posteDAO.updatePoste(p);

            if (u == null || p == null) {
                new Alert(Alert.AlertType.WARNING, "Utilisateur ou Poste invalide.").show();
                return;
            }

            LocalDateTime dt = LocalDateTime.of(date, time);
            Demande d = new Demande(0, duree, dt, u, p);
            demandeDAO.ajouterDemande(d);
            loadDemandes();

            // Réinitialiser les champs
            dureeField.clear();
            utilisateurIdField.clear();
            posteIdField.clear();
            heureField.clear();
            dateField.setValue(null);

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR,
                    "Veuillez entrer des nombres valides pour la durée, l'utilisateur et le poste.").show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout de la demande.").show();
        }

    }

    @FXML
    private void rechercherParDate() {
        LocalDate date = searchDatePicker.getValue();
        if (date == null) {
            new Alert(Alert.AlertType.INFORMATION, "Choisissez une date.").show();
            return;
        }
        List<Demande> list = demandeDAO.searchByDate(date);
        demandeTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void resetRecherche() {
        searchDatePicker.setValue(null);
        loadDemandes();
    }

    // ✏️ Modifier
    @FXML
    private void modifierDemande() {
        // Récupère la ligne sélectionnée
        Demande selected = demandeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez une demande à modifier.").show();
            return;
        }

        // Vérifie que les champs sont remplis
        if (dureeField.getText().isEmpty() || dateField.getValue() == null ||
                heureField.getText().isEmpty() || utilisateurIdField.getText().isEmpty() ||
                posteIdField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Tous les champs doivent être remplis.").show();
            return;
        }

        try {
            int duree = Integer.parseInt(dureeField.getText().trim());
            LocalDate date = dateField.getValue();
            LocalTime time = LocalTime.parse(heureField.getText().trim());
            int userId = Integer.parseInt(utilisateurIdField.getText().trim());
            int posteId = Integer.parseInt(posteIdField.getText().trim());

            // Récupère les entités liées
            Utilisateur u = utilisateurDAO.getUtilisateurById(userId);
            Poste p = posteDAO.getPosteById(posteId);

            if (u == null || p == null) {
                new Alert(Alert.AlertType.ERROR, "Utilisateur ou poste introuvable.").show();
                return;
            }

            // ✅ Met à jour l'objet sélectionné AVANT l'appel DAO
            selected.setDuree(duree);
            selected.setDateHeure(LocalDateTime.of(date, time));
            selected.setUtilisateur(u);
            selected.setPoste(p);

            // Appel DAO
            demandeDAO.modifierDemande(selected);

            // Rafraîchit la table
            loadDemandes();

            // Réinitialise les champs
            clearFields();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Durée, ID utilisateur et ID poste doivent être des nombres.").show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification.").show();
        }
    }

    private void clearFields() {
        dureeField.clear();
        heureField.clear();
        utilisateurIdField.clear();
        posteIdField.clear();
        dateField.setValue(null);
    }

    @FXML
    private void supprimerDemande() {
        Demande d = demandeTable.getSelectionModel().getSelectedItem();
        if (d == null) {
            new Alert(Alert.AlertType.ERROR, "Sélectionnez une demande à supprimer").show();
            return;
        }

        // 🔔 Créer la boîte de confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer cette demande ?");
        confirm.setContentText(
                "ID : " + d.getId() +
                        "\nUtilisateur : " + (d.getUtilisateur() != null ? d.getUtilisateur().getNom() : "—") +
                        "\nPoste : " + (d.getPoste() != null ? d.getPoste().getReference() : "—") +
                        "\nDurée : " + d.getDuree() + " min");

        // Boutons personnalisés
        ButtonType ok = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(ok, cancel);

        // Attendre la réponse de l'utilisateur
        confirm.showAndWait().ifPresent(type -> {
            if (type == ok) {
                // ✅ Suppression uniquement si confirmé
                demandeDAO.supprimerDemande(d.getId());
                loadDemandes();
                new Alert(Alert.AlertType.CONFIRMATION, "Demande supprimé avec succès !!s").show();
            }
            // sinon, aucune action
        });
    }
}

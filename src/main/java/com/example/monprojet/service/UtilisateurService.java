package com.example.monprojet.service;

import com.example.monprojet.dao.UtilisateurDAO;
import com.example.monprojet.model.Utilisateur;
import com.example.monprojet.Util.PasswordUtils;
import com.example.monprojet.Util.Validator;
import com.example.monprojet.Util.Logger;

import java.time.LocalDate;
import java.util.List;

public class UtilisateurService {

    private final UtilisateurDAO utilisateurDAO;

    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    // Inscription avec validation + hash du mot de passe
    public boolean inscrireUtilisateur(String nom, String prenom, String password) {
        if (!Validator.isNotEmpty(nom) || !Validator.isNotEmpty(prenom)) {
            Logger.logInfo("Nom ou prénom invalide.");
            return false;
        }
        if (!Validator.isValidPassword(password)) {
            Logger.logInfo("Mot de passe trop court (min 6 caractères).");
            return false;
        }

        String hashed = PasswordUtils.hashPassword(password);
        Utilisateur u = new Utilisateur(0, nom, prenom, hashed, LocalDate.now());
        utilisateurDAO.ajouterUtilisateur(u);

        Logger.logInfo("Utilisateur inscrit avec succès: " + nom + " " + prenom);
        return true;
    }

    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurDAO.getUtilisateurs();
    }

    public boolean connecterUtilisateur(String nom, String password) {
        List<Utilisateur> utilisateurs = utilisateurDAO.getUtilisateurs();
        for (Utilisateur u : utilisateurs) {
            if (u.getNom().equalsIgnoreCase(nom)) {
                boolean ok = PasswordUtils.checkPassword(password, u.getPassword());
                if (ok) {
                    Logger.logInfo("Connexion réussie pour: " + nom);
                    return true;
                } else {
                    Logger.logInfo("Mot de passe incorrect pour: " + nom);
                    return false;
                }
            }
        }
        Logger.logInfo("Utilisateur introuvable: " + nom);
        return false;
    }

    public void supprimerUtilisateur(int id) {
        utilisateurDAO.deleteUtilisateur(id);
        Logger.logInfo("Utilisateur supprimé avec id=" + id);
    }
}

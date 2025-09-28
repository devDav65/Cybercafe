package com.example.monprojet.service;

import com.example.monprojet.dao.DemandeDAO;
import com.example.monprojet.dao.PosteDAO;
import com.example.monprojet.dao.UtilisateurDAO;
import com.example.monprojet.model.Demande;
import com.example.monprojet.model.Poste;
import com.example.monprojet.model.Utilisateur;
import com.example.monprojet.Util.Logger;

import java.time.LocalDateTime;
import java.util.List;

public class DemandeService {

    private final DemandeDAO demandeDAO;
    private final PosteDAO posteDAO;
    private final UtilisateurDAO utilisateurDAO;

    private final int DUREE_MAX_HEURES = 3;

    public DemandeService() {
        this.demandeDAO = new DemandeDAO();
        this.posteDAO = new PosteDAO();
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public boolean creerDemande(int utilisateurId, int posteId, int duree, LocalDateTime dateHeure) {
        if (duree <= 0 || duree > DUREE_MAX_HEURES) {
            Logger.logInfo("Durée de demande invalide.");
            return false;
        }

        Utilisateur u = utilisateurDAO.getUtilisateurById(utilisateurId);
        Poste p = posteDAO.getPosteById(posteId);

        if (u == null || p == null) {
            Logger.logInfo("Utilisateur ou poste introuvable.");
            return false;
        }

        if ("maintenance".equalsIgnoreCase(p.getEtat())) {
            Logger.logInfo("Poste en maintenance, demande refusée.");
            return false;
        }

        Demande d = new Demande(0, duree, dateHeure, u, p);
        demandeDAO.ajouterDemande(d);

        Logger.logInfo("Demande créée pour user=" + utilisateurId);
        return true;
    }

    public List<Demande> listerDemandes() {
        return demandeDAO.getAllDemandes();
    }

    public void supprimerDemande(int id) {
        demandeDAO.supprimerDemande(id);
        Logger.logInfo("Demande supprimée id=" + id);
    }
}

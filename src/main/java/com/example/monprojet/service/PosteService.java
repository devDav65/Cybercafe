package com.example.monprojet.service;

import com.example.monprojet.dao.PosteDAO;
import com.example.monprojet.model.Poste;
import com.example.monprojet.Util.Validator;
import com.example.monprojet.Util.Logger;

import java.util.List;

public class PosteService {

    private final PosteDAO posteDAO;

    public PosteService() {
        this.posteDAO = new PosteDAO();
    }

    public boolean ajouterPoste(String reference, String adresseIp, String emplacement, String etat) {
        if (!Validator.isNotEmpty(reference)) {
            Logger.logInfo("Référence invalide.");
            return false;
        }
        // Ici tu pourrais ajouter Validator.isValidIP() plus tard si tu l’ajoutes
        Poste p = new Poste(0, reference, adresseIp, emplacement, etat == null ? "disponible" : etat);
        posteDAO.ajouterPoste(p);

        Logger.logInfo("Poste ajouté: " + reference);
        return true;
    }

    public List<Poste> listerPostes() {
        return posteDAO.getAllPostes();
    }

    public Poste trouverPosteParId(int id) {
        return posteDAO.getPosteById(id);
    }

    public void mettreEnMaintenance(int id) {
        Poste p = posteDAO.getPosteById(id);
        if (p != null) {
            p.setEtat("maintenance");
            posteDAO.updatePoste(p);
            Logger.logInfo("Poste id=" + id + " mis en maintenance.");
        }
    }

    public void libererPoste(int id) {
        Poste p = posteDAO.getPosteById(id);
        if (p != null) {
            p.setEtat("disponible");
            posteDAO.updatePoste(p);
            Logger.logInfo("Poste id=" + id + " libéré.");
        }
    }
}

package com.example.monprojet.service;

import com.example.monprojet.dao.ReglefiltreDAO;
import com.example.monprojet.model.Reglefiltre;
import com.example.monprojet.Util.Logger;

import java.util.List;

public class RegleFiltreService {

    private final ReglefiltreDAO regleDAO;

    public RegleFiltreService() {
        this.regleDAO = new ReglefiltreDAO();
    }

    public void ajouterRegle(String ip, String protocole, String action) {
        Reglefiltre r = new Reglefiltre(0, ip, protocole, action);
        regleDAO.ajouterRegle(r);
        Logger.logInfo("Règle ajoutée: " + ip + " " + protocole + " -> " + action);
    }

    public List<Reglefiltre> listerRegles() {
        return regleDAO.getAllRegles();
    }

    public void supprimerRegle(int id) {
        regleDAO.deleteRegle(id);
        Logger.logInfo("Règle supprimée id=" + id);
    }

    public String evaluerPaquet(String ipSource, String protocole) {
        List<Reglefiltre> regles = regleDAO.getAllRegles();
        for (Reglefiltre r : regles) {
            if (r.getIp().equals(ipSource) && r.getProtocole().equalsIgnoreCase(protocole)) {
                return r.getAction();
            }
        }
        return null;
    }
}

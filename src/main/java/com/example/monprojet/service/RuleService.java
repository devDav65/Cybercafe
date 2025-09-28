package com.example.monprojet.service;

import com.example.monprojet.dao.ReglefiltreDAO;
import com.example.monprojet.model.Reglefiltre;
import com.example.monprojet.model.Packet;

import java.util.List;

public class RuleService {

    private final ReglefiltreDAO dao = new ReglefiltreDAO();
    private List<Reglefiltre> cache;

    public RuleService() {
        reloadCache();
    }

    public void reloadCache() {
        cache = dao.getAllRegles();
    }

    /**
     * Retourne "ALLOW" ou "BLOCK"
     */
    public String apply(Packet p) {
        // simple: si IP source match une règle -> action; si protocole match -> action;
        // priority: IP then protocole
        for (Reglefiltre r : cache) {
            if (r.getIp() != null && !r.getIp().isEmpty()) {
                if (r.getIp().equals(p.getIpsource()) || r.getIp().equals(p.getIpdestination())) {
                    return r.getAction().toUpperCase();
                }
            }
        }
        for (Reglefiltre r : cache) {
            if (r.getProtocole() != null && r.getProtocole().equalsIgnoreCase(p.getProtocole())) {
                return r.getAction().toUpperCase();
            }
        }
        return "ALLOW"; // default
    }
}

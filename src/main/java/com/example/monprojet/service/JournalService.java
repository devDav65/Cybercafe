package com.example.monprojet.service;

import com.example.monprojet.dao.JournalDAO;
import com.example.monprojet.model.Journal;
import com.example.monprojet.Util.Logger;

import java.time.LocalTime;
import java.util.List;

public class JournalService {

    private final JournalDAO journalDAO;

    public JournalService() {
        this.journalDAO = new JournalDAO();
    }

    public void enregistrerEntree(String ipSource, String ipDestination, String protocole, LocalTime horlotage,
            String action) {
        Journal j = new Journal(0, ipSource, ipDestination, protocole, horlotage, action);
        journalDAO.ajouterJournal(j);
        Logger.logInfo("Journal enregistré: " + ipSource + " -> " + ipDestination);
    }

    public List<Journal> listerJournaux() {
        return journalDAO.getAllJournaux();
    }
}

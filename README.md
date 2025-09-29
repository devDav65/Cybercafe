# 🖥️ Cybercafé – Application de Gestion et de Filtrage Réseau

## 📜 Description
**Cybercafé** est une application Java/JavaFX pour la **gestion d’un cybercafé** et le **filtrage réseau en temps réel**.  
Elle permet :
- La gestion des **postes**, **utilisateurs**, **réservations** et **demandes de connexion**.
- La **surveillance du trafic réseau** et le blocage de paquets selon des **règles de filtrage** configurables.
- La **journalisation en direct** des paquets bloqués ou autorisés.

---

## ✨ Fonctionnalités principales
- 🔑 **Gestion des utilisateurs** : ajout, modification, suppression, recherche.
- 🖥️ **Gestion des postes** : suivi de l’état (libre/occupé) et des connexions.
- 📅 **Réservations** : création, consultation, suppression avec confirmation.
- 📨 **Demandes de connexion** : notification automatique quand le temps est écoulé.
- 🛡️ **Règles de filtrage réseau** : blocage d’IP ou de protocole (TCP/UDP/ICMP).
- 📡 **Surveillance réseau** :
  - Lecture de paquets simulés (JSON/NDJSON).
  - Détection en temps réel des paquets à bloquer.
  - Journalisation automatique dans la base de données.
- 📊 **Tableau de bord** : affichage en direct des journaux.

---

## 🏗️ Architecture
- **Frontend** : JavaFX + CSS (interface moderne et responsive).
- **Backend** : Java 21+, JDBC, DAO (pattern MVC).
- **Base de données** : MySQL/MariaDB.
- **Service réseau** : `PacketProcessor` lit des fichiers JSON/NDJSON générés par un script Python (`simulate_packets.py`) ou par un capteur réseau.

---

## 📂 Structure du projet
```
Cybercafe/
├─ src/main/java/com/example/monprojet/
│  ├─ controllers/   # Contrôleurs JavaFX
│  ├─ dao/           # Accès base de données (DAO)
│  ├─ model/         # Entités (Utilisateur, Poste, Journal, Reglefiltre…)
│  └─ service/       # PacketProcessor, logique de filtrage réseau
├─ src/main/resources/
│  ├─ View/          # Fichiers FXML
│  ├─ css/           # Feuilles de style CSS
│  └─ database.sql   # Script de création de la base
└─ README.md
```

---

## ⚙️ Installation et exécution

### 1️⃣ Prérequis
- **Java 21+**
- **Maven 3+**
- **MySQL/MariaDB** (base `cybercafe`)

### 2️⃣ Cloner le dépôt
```bash
git clone https://github.com/devDav65/Cybercafe.git
cd Cybercafe
```

### 3️⃣ Configurer la base
- Importer `database.sql` (dans `src/main/resources`) dans MySQL.
- Vérifier les paramètres de connexion dans `Database.java` (URL, utilisateur, mot de passe).

### 4️⃣ Lancer l’application
```bash
mvn clean javafx:run
```

---

## 🧪 Simulation du trafic réseau
Pour tester la capture en temps réel :

```bash
python simulate_packets.py
```

Le script génère un flux de paquets dans `simulated_packets.jsonl`.  
`PacketProcessor` lit ce fichier en continu et met à jour le **journal** dans l’interface.

---

## 🚀 Améliorations prévues
- Support d’un vrai sniffing réseau (ex: Scapy, PCAP).
- Gestion avancée des tarifs et paiements.
- Notifications push sur mobile.

---

## 👤 Auteur
**KOKA Essowaba David**  
- 🏙️ Lomé, Togo  
- 💼 Étudiant & Développeur passionné de cybersécurité  


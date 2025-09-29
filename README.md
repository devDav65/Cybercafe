🖥️ Cybercafé – Application de Gestion et de Filtrage Réseau
📜 Description

Cybercafé est une application Java/JavaFX destinée à la gestion d’un cybercafé et au filtrage réseau en temps réel.
Elle combine :

Gestion des postes, utilisateurs, réservations et demandes de connexion.

Surveillance réseau : détection et blocage de paquets selon des règles de filtrage configurables.

Journalisation temps réel des paquets bloqués ou autorisés.

L’application est développée avec JavaFX pour l’interface graphique et utilise MySQL comme base de données.

✨ Fonctionnalités principales

🔑 Gestion des utilisateurs : ajout, modification, suppression, recherche.

🖥️ Gestion des postes : suivi de l’état (libre/occupé) et des connexions.

📅 Réservations : création, consultation, suppression avec confirmation.

📨 Demandes de connexion : notification automatique lorsque le temps est écoulé.

🛡️ Règles de filtrage réseau : blocage d’IP ou de protocole (TCP/UDP/ICMP).

📡 Surveillance réseau :

Lecture de paquets simulés (JSON/NDJSON).

Détection en temps réel des paquets correspondant aux règles de blocage.

Journalisation automatique dans la base de données.

📊 Tableau de bord : visualisation en direct des journaux de paquets.

🏗️ Architecture

Frontend : JavaFX + CSS personnalisé (UI moderne et responsive).

Backend : Java 21+, JDBC, DAO (pattern MVC).

Base de données : MySQL/MariaDB.

Service réseau : PacketProcessor lit des fichiers JSON/NDJSON générés par un script Python (simulate_packets.py) ou par un capteur réseau.
📂 Structure du projet
Cybercafe/
├─ src/main/java/com/example/monprojet/
│  ├─ controllers/   # Contrôleurs JavaFX
│  ├─ dao/           # Data Access Objects
│  ├─ model/         # Entités (Utilisateur, Poste, Journal, Reglefiltre…)
│  └─ service/       # PacketProcessor, logique de filtrage réseau
├─ src/main/resources/
│  ├─ View/          # Fichiers FXML
│  ├─ css/           # Feuilles de style CSS
│  └─ database.sql   # Script de création de la base
└─ README.md
⚙️ Installation et exécution
1️⃣ Prérequis

Java 21+

Maven 3+

MySQL/MariaDB (crée une base cybercafe)

2️⃣ Cloner le dépôt
git clone https://github.com/devDav65/Cybercafe
cd Cybercafe
3️⃣ Configurer la base

Importer database.sql (fourni dans resources) dans MySQL.

Vérifier les paramètres de connexion dans Database.java (URL, utilisateur, mot de passe).

4️⃣ Lancer l’application
mvn clean javafx:run
🧪 Simulation du trafic réseau

Pour tester la capture en temps réel :
python simulate_packets.py
Le script génère un flux de paquets dans simulated_packets.jsonl.
PacketProcessor lit ce fichier en continu et met à jour le journal dans l’interface.
🚀 Améliorations prévues

Support d’un vrai sniffing réseau (ex: Scapy, PCAP).

Gestion avancée des tarifs et paiements.

Notifications push sur mobile.

👤 Auteur

KOKA Essowaba David

🏙️ Lomé, Togo

💼 Étudiant & Développeur passionné de cybersécurité
📝 Licence

Projet libre pour un usage éducatif.

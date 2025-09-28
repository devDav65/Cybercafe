package com.example.monprojet;

import java.nio.file.Paths;

import com.example.monprojet.controllers.SessionScheduler;
import com.example.monprojet.service.PacketProcessor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    SessionScheduler scheduler = new SessionScheduler();

    PacketProcessor processor = new PacketProcessor();

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/LoginView.fxml"));
        Scene scene = new Scene(loader.load());

        processor.startPolling(Paths.get("chemin/vers/tes/fichiers"), 5);

        scheduler.start();

        primaryStage.setTitle("GESTION CYBERCAFÉ");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        scheduler.stop();
        processor.stop();

    }

    public static void main(String[] args) {
        launch(args); // lance JavaFX
    }
}

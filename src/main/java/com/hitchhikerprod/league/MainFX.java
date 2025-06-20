package com.hitchhikerprod.league;

import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class MainFX extends Application {
    private Stage stage;
    private Parent root;
    private LeagueController controller;

    @FXML
    private ProgressBar loadProgress;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        final URL fxmlUrl = getClass().getResource("main.fxml");
        final URL cssUrl = getClass().getResource("styles.css");
        if (fxmlUrl == null) {
            throw new RuntimeException("Can't load FXML file");
        }
        if (cssUrl == null) {
            throw new RuntimeException("Can't load styles file");
        }

        final FXMLLoader loader = new FXMLLoader(fxmlUrl);
        this.root = loader.load();
//        this.root.addEventHandler(LeagueController.LeagueFileReader.ProgressEvent.ANY,
//                event -> controller.updateProgressBar(event));

        this.controller = loader.getController();
        this.controller.setApplication(this);

        final Scene scene = new Scene(this.root);
        scene.getStylesheets().add(cssUrl.toExternalForm());
        this.stage.setTitle("LeagueSim");
        this.stage.setScene(scene);
        this.stage.show();
    }

    void fireEvent(Event event) {
        this.root.fireEvent(event);
    }

    File runOpenFileDialog() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open League File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("YAML Files", "*.yml", "*.yaml"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        return fileChooser.showOpenDialog(this.stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

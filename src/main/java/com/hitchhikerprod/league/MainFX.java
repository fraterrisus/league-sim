package com.hitchhikerprod.league;

import com.hitchhikerprod.league.ui.RootWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MainFX extends Application {
    private LeagueController controller;
    private Stage stage;
    public RootWindow root;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        final URL cssUrl = getClass().getResource("styles.css");
        if (cssUrl == null) {
            throw new RuntimeException("Can't load styles file");
        }

        this.controller = new LeagueController();
        this.controller.setApplication(this);

        this.root = new RootWindow(controller);
        final Scene scene = new Scene(this.root.asParent());
        scene.getStylesheets().add(cssUrl.toExternalForm());
        this.stage.setTitle("LeagueSim");
        this.stage.setScene(scene);
        this.stage.show();
    }

    public RootWindow ui() {
        return root;
    }

    private void loadFromFXML(String fxmlFile) throws IOException {
        final URL fxmlUrl = getClass().getResource(fxmlFile);
        if (fxmlUrl == null) {
            throw new RuntimeException("Can't load FXML file");
        }

        final FXMLLoader loader = new FXMLLoader(fxmlUrl);
        this.root = loader.load();
        this.controller = loader.getController();
        this.controller.setApplication(this);
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

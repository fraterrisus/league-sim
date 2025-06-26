package com.hitchhikerprod.league;

import com.hitchhikerprod.league.beans.Division;
import com.hitchhikerprod.league.definitions.UFA2025;
import com.hitchhikerprod.league.tasks.ReadLeagueFile;
import com.hitchhikerprod.league.ui.RootWindow;
import com.hitchhikerprod.league.ui.StandingsPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LeagueApp extends Application {
    private Stage stage;
    private RootWindow root;

    private League league;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        final URL cssUrl = getClass().getResource("styles.css");
        if (cssUrl == null) {
            throw new RuntimeException("Can't load styles file");
        }

        root = RootWindow.getInstance();
        root.setApplication(this);
        root.asParent().getStylesheets().add(cssUrl.toExternalForm());
        final Scene scene = new Scene(root.asParent());
        this.stage.setTitle("LeagueSim");
        this.stage.setScene(scene);
        this.stage.show();

        root.noLeaguePane.label.setText("No League file loaded.\nUse File>Open to read a League file.");
    }

    public Stage getStage() {
        return stage;
    }

    private File runOpenFileDialog() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open League File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("YAML Files", "*.yml", "*.yaml"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        return fileChooser.showOpenDialog(this.stage);
    }

    public void menuOpen() {
        final File selectedFile = runOpenFileDialog();
        if (selectedFile == null) return;

        if (league != null) {
            // pop an alert asking for confirmation
            return;
        }
        // app.root.activate(NO_LEAGUE);
        final Label progressLabel = root.noLeaguePane.label;
        final ProgressBar progressBar = root.noLeaguePane.progressBar;
        progressBar.setVisible(true);

        final ReadLeagueFile reader = new ReadLeagueFile(selectedFile);
        progressLabel.textProperty().bind(reader.titleProperty());
        progressBar.progressProperty().bind(reader.progressProperty());
        reader.setOnFailed(event -> {
            progressBar.setVisible(false);
            progressLabel.textProperty().unbind();
            final Alert alert = new Alert(Alert.AlertType.ERROR, reader.getException().getMessage() );
            alert.showAndWait();
        });
        reader.setOnSucceeded(event -> {
            progressBar.setVisible(false);
            progressLabel.textProperty().unbind();
            try {
                league = reader.get();
                progressLabel.setText("Done.");
            } catch (InterruptedException | ExecutionException e) {
                final Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
                return;
            }
            openStandings();
        });
        new Thread(reader).start();
    }

    public void menuQuit() {
        Platform.exit();
    }

    public void openStandings() {
        final StandingsPane standingsPane = root.standingsPane;
        final int latestCompleteMatchDay = league.getLatestCompleteMatchDay();
        final Map<Division, List<UFA2025.TeamData>> divisionTables = league.getDivisionTables(latestCompleteMatchDay);

        standingsPane.setDivisions(divisionTables);
        standingsPane.setStandings(divisionTables);
        standingsPane.setMatchDays(league.getMatchDays(), latestCompleteMatchDay);
        standingsPane.setGamesList(league.getGames(latestCompleteMatchDay));

        standingsPane.setMatchDayCallback(ev -> {
            final int matchDayIndex = standingsPane.getSelectedMatchDay();
            standingsPane.setStandings(league.getDivisionTables(matchDayIndex));
            standingsPane.setGamesList(league.getGames(matchDayIndex));
        });

        standingsPane.setRegenerateTablesCallback(ev -> {
            final int matchDayIndex = standingsPane.getSelectedMatchDay();
            standingsPane.setStandings(league.getDivisionTables(matchDayIndex));
        });

        root.activate(RootWindow.OpenWindow.STANDINGS);
        stage.sizeToScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

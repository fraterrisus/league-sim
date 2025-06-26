package com.hitchhikerprod.league;

import com.hitchhikerprod.league.beans.Division;
import com.hitchhikerprod.league.definitions.UFA2025;
import com.hitchhikerprod.league.tasks.ReadLeagueFile;
import com.hitchhikerprod.league.tasks.SaveLeagueFile;
import com.hitchhikerprod.league.ui.RootWindow;
import com.hitchhikerprod.league.ui.StandingsPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class LeagueApp extends Application {
    private Stage stage;
    private RootWindow root;

    private League league;
    private String leagueFileName;

    public static void main(String[] args) {
        launch(args);
    }

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

    public void menuOpen() {
        if (league != null) {
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Do you want to close the existing league and open a new one?");
            final Optional<ButtonType> response = alert.showAndWait();
            if (response.isEmpty() || response.get() != ButtonType.OK) return;
        }

        final File selectedFile = runOpenFileDialog();
        if (selectedFile == null) return;

        if (!updateFileName(selectedFile)) return;

        runOpenTask(selectedFile);
    }

    public void menuSaveAs() {
        if (league == null) return;

        final File outputFile = runSaveFileDialog();
        if (outputFile == null) return;
        if (!updateFileName(outputFile)) return;

        runSaveTask(outputFile);
    }

    public void menuSave() {
        if (league == null || leagueFileName == null) return;

        final File outputFile = new File(leagueFileName);

        runSaveTask(outputFile);
    }

    public void menuQuit() {
        Platform.exit();
    }

    private File runOpenFileDialog() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open League File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("YAML Files", "*.yml", "*.yaml"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        return fileChooser.showOpenDialog(this.stage);
    }

    private File runSaveFileDialog() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save League File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("YAML Files", "*.yml", "*.yaml"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        return fileChooser.showSaveDialog(this.stage);
    }

    private boolean updateFileName(File file) {
        try {
            leagueFileName = file.getCanonicalPath();
        } catch (IOException e) {
            final Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
            leagueFileName = null;
            return false;
        }
        return true;
    }

    private void runOpenTask(File inputFile) {
        // app.root.activate(NO_LEAGUE);
        final Label progressLabel = root.noLeaguePane.label;
        final ProgressBar progressBar = root.noLeaguePane.progressBar;
        progressBar.setVisible(true);

        final ReadLeagueFile reader = new ReadLeagueFile(inputFile);
        progressLabel.textProperty().bind(reader.titleProperty());
        progressBar.progressProperty().bind(reader.progressProperty());
        reader.setOnFailed(event -> {
            progressBar.setVisible(false);
            progressLabel.textProperty().unbind();
            final Alert alert = new Alert(Alert.AlertType.ERROR, reader.getException().getMessage());
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
            root.menuBar.allowSave();
            openStandings();
        });
        new Thread(reader).start();
    }

    private void runSaveTask(File outputFile) {
        final SaveLeagueFile writer = new SaveLeagueFile(league, outputFile);
        writer.setOnFailed(event -> {
            final Alert alert = new Alert(Alert.AlertType.ERROR, writer.getException().getMessage() );
            alert.showAndWait();
        });
        new Thread(writer).start();
    }

    private void openStandings() {
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
}

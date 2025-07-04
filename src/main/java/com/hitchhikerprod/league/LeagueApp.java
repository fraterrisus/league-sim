package com.hitchhikerprod.league;

import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.definitions.League;
import com.hitchhikerprod.league.tasks.ReadLeagueFile;
import com.hitchhikerprod.league.tasks.SaveLeagueFile;
import com.hitchhikerprod.league.ui.EditGamesDialog;
import com.hitchhikerprod.league.ui.EditMatchDaysDialog;
import com.hitchhikerprod.league.ui.MatchDayPane;
import com.hitchhikerprod.league.ui.RootWindow;
import com.hitchhikerprod.league.ui.StandingsPane;
import com.hitchhikerprod.league.ui.TeamGamesWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
        TeamGamesWindow.getInstance().setStylesheets(cssUrl.toExternalForm());

        final Scene scene = new Scene(root.asParent());
        this.stage.setTitle("LeagueSim");
        this.stage.setScene(scene);
        this.stage.show();

        root.setStatusMessage("No League file loaded. Use File>Open to read a League file.");
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

    public void menuEditDivisions() {

    }

    public void menuEditGames() {
        final MatchDayPane matchDayPane = MatchDayPane.getInstance();
        final int matchDayIndex = matchDayPane.getSelectedMatchDay();
        new EditGamesDialog(stage, league, matchDayIndex).showAndWait();
        matchDayPane.setGamesList(league, matchDayIndex);
        stage.sizeToScene();
    }

    public void menuEditMatchDays() {
        new EditMatchDaysDialog(stage, league).showAndWait();
        MatchDayPane.getInstance().setMatchDays(league);
    }

    public void menuQuit() {
        Platform.exit();
    }

    public void contextMenuShowGames(TableView<LeagueTeamData> parent) {
        final LeagueTeamData target = parent.getFocusModel().getFocusedItem();
        final TeamGamesWindow gamesWindow = TeamGamesWindow.getInstance();
        gamesWindow.setGames(league, target);
        gamesWindow.show();
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
        root.setStatusMessage("Loading League file...", true);
        final DoubleProperty progressProperty = root.getProgressProperty();
        final StringProperty statusProperty = root.getStatusProperty();

        final ReadLeagueFile reader = new ReadLeagueFile(inputFile);
        statusProperty.bind(reader.titleProperty());
        progressProperty.bind(reader.progressProperty());
        reader.setOnFailed(event -> {
            statusProperty.unbind();
            progressProperty.unbind();
            root.setStatusMessage("Error.", false);
            final Alert alert = new Alert(Alert.AlertType.ERROR, reader.getException().getMessage());
            alert.showAndWait();
        });
        reader.setOnSucceeded(event -> {
            statusProperty.unbind();
            progressProperty.unbind();
            try {
                league = reader.get();
                root.setStatusMessage("Loaded " + inputFile.getName(), false);
            } catch (InterruptedException | ExecutionException e) {
                root.setStatusMessage("Error.", false);
                final Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
                return;
            }
            root.allowSave();
            openStandings();
        });
        new Thread(reader).start();
    }

    private void runSaveTask(File outputFile) {
        final SaveLeagueFile writer = new SaveLeagueFile(league, outputFile);
        writer.setOnSucceeded(event -> {
            root.setStatusMessage("Saved " + outputFile.getName(), false);
        });
        writer.setOnFailed(event -> {
            final Alert alert = new Alert(Alert.AlertType.ERROR, writer.getException().getMessage() );
            alert.showAndWait();
        });
        new Thread(writer).start();
    }

    private void openStandings() {
        final RootWindow mainWindow = RootWindow.getInstance();
        final StandingsPane standingsPane = StandingsPane.getInstance();
        final MatchDayPane matchDayPane = MatchDayPane.getInstance();
        final int latestCompleteMatchDay = league.getLatestCompleteMatchDay();

        standingsPane.buildDivisionsPane(league, latestCompleteMatchDay);
        standingsPane.setStandings(league, latestCompleteMatchDay);
        matchDayPane.setMatchDays(league, latestCompleteMatchDay);
        matchDayPane.setGamesList(league, latestCompleteMatchDay);

        matchDayPane.setMatchDayCallback(ev -> {
            final int matchDayIndex = matchDayPane.getSelectedMatchDay();
            if (matchDayIndex == -1) return;
            standingsPane.setStandings(league, matchDayIndex);
            matchDayPane.setGamesList(league, matchDayIndex);
        });

        mainWindow.setRegenerateTablesCallback(ev -> {
            final int matchDayIndex = matchDayPane.getSelectedMatchDay();
            if (matchDayIndex == -1) return;
            standingsPane.setStandings(league, matchDayIndex);
        });

        stage.sizeToScene();
    }
}

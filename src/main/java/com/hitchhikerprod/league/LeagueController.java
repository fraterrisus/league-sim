package com.hitchhikerprod.league;

import com.hitchhikerprod.league.tasks.ReadLeagueFile;
import com.hitchhikerprod.league.ui.RootWindow;
import com.hitchhikerprod.league.ui.StandingsPane;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class LeagueController {
    private LeagueApp app;
    private League league;

    public void setApplication(LeagueApp app) {
        this.app = app;
    }

    public void menuOpen() {
        final File selectedFile = app.runOpenFileDialog();
        if (selectedFile == null) return;

        if (league != null) {
            // pop an alert asking for confirmation
            return;
        }
        // app.root.activate(NO_LEAGUE);
        final Label progressLabel = app.root.noLeaguePane.label;
        final ProgressBar progressBar = app.root.noLeaguePane.progressBar;
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
        final StandingsPane standingsPane = app.root.standingsPane;
        final int latestCompleteMatchDay = league.getLatestCompleteMatchDay();
        standingsPane.buildPanels(league.getDivisionTables(latestCompleteMatchDay));
        standingsPane.setMatchDays(league.getMatchDays(), latestCompleteMatchDay);
        standingsPane.setMatchDayCallback(ev -> {
            final int matchDayIndex = standingsPane.getSelectedMatchDay();
            standingsPane.buildPanels(league.getDivisionTables(matchDayIndex));
        });

        app.root.activate(RootWindow.OpenWindow.STANDINGS);
        app.stage.sizeToScene();
    }
}

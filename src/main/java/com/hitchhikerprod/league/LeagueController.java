package com.hitchhikerprod.league;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LeagueController {
    @FXML
    private Label label;

    @FXML
    private ProgressBar loadProgress;

    @FXML
    private MenuBar menuBar;

    private MainFX app;

    private League league;

    public void setApplication(MainFX app) {
        this.app = app;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public void initialize() {
        final String javaVersion = System.getProperty("java.version");
        final String javafxVersion = System.getProperty("javafx.version");
        label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".");
        loadProgress.setProgress(0.0);
    }

    public void menuOpen() {
        final File selectedFile = app.runOpenFileDialog();
        if (selectedFile == null) return;

        // check to make sure that there's no existing file

        final ReadLeagueFile reader = new ReadLeagueFile(selectedFile);
        label.textProperty().bind(reader.titleProperty());
        loadProgress.progressProperty().bind(reader.progressProperty());
        reader.setOnSucceeded(event -> {
            try {
                this.league = reader.get();
                label.textProperty().unbind();
                label.setText("Done.");
            } catch (InterruptedException | ExecutionException e) {
                System.err.println(e);
            }
        });
        reader.setOnFailed(event -> {
            label.textProperty().unbind();
            label.setText("Failed.");
        });

        final Thread readerThread = new Thread(reader);
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void menuQuit() {
        Platform.exit();
    }

    public static final class ReadLeagueFile extends Task<League> {
        final File inputFile;

        ReadLeagueFile(File inputFile) {
            this.inputFile = inputFile;
        }

        private void sleepHelper(long ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }

        @Override
        public League call() {
            final long fileSize = inputFile.length();
            final List<String> document = new ArrayList<>();
            long progress = 0;

            updateTitle("Reading league data file...");

            try (final BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile.getPath()), StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sleepHelper(5);
                    if (isCancelled()) return null;
                    document.add(line);
                    progress += line.length() + 1;
                    updateProgress(progress, fileSize);
                }
                updateProgress(fileSize, fileSize);
            } catch (IOException e) {
                System.err.println(e);
                return null;
            }

            updateTitle("Parsing games...");
            updateProgress(-1, 1);
            final League l = LeagueFactory.fromYaml(String.join("\n", document));
            sleepHelper(500);
            updateTitle("Complete.");
            updateProgress(1, 1);
            return l;
        }
    }
}

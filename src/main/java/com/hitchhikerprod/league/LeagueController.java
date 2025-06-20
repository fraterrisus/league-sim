package com.hitchhikerprod.league;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    private MainFX app;

    public void setApplication(MainFX app) {
        this.app = app;
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

        // label.setText(selectedFile.getName());

        final ReadLeagueFile reader = new ReadLeagueFile(selectedFile);
        loadProgress.progressProperty().bind(reader.progressProperty());
        reader.setOnSucceeded(event -> {
            try {
                List<String> document = reader.get();
                label.setText(document.getFirst());
            } catch (InterruptedException | ExecutionException e) {
                return;
            }
        });

        final Thread readerThread = new Thread(reader);
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void menuQuit() {
        Platform.exit();
    }

    public static final class ReadLeagueFile extends Task<List<String>> {
        final File inputFile;

        ReadLeagueFile(File inputFile) {
            this.inputFile = inputFile;
        }

        @Override
        public List<String> call() {
            final long fileSize = inputFile.length();
            final List<String> document = new ArrayList<>();
            long progress = 0;

            try (final BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile.getPath()), StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (isCancelled()) return null;
                    document.add(line);
                    progress += line.length() + 1;
                    updateProgress(progress, fileSize);
                }
                updateProgress(fileSize, fileSize);
                return document;
            } catch (IOException e) {
                System.err.println(e);
                return null;
            }
        }
    }
}

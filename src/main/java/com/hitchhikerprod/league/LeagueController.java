package com.hitchhikerprod.league;

import com.hitchhikerprod.league.util.AtomicStringList;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventType;
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

public class LeagueController {
    @FXML
    private Label label;

    @FXML
    private ProgressBar loadProgress;

    private MainFX app;

    private final AtomicStringList fileData = new AtomicStringList();
    private long fileSize = 0;

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

        this.fileSize = selectedFile.length();
        label.setText(selectedFile.getName());
        final LeagueFileReader reader = new LeagueFileReader(app, selectedFile, fileData);
        new Thread(reader).start();
    }

    public void menuQuit() {
        Platform.exit();
    }

    public void updateProgressBar(Event event) {
        LeagueFileReader.ProgressEvent pEvent = (LeagueFileReader.ProgressEvent) event;
        long progress = pEvent.getProgress();
        System.out.println("Update event (" + progress + "/" + fileSize + ")");
        loadProgress.setProgress((double)progress / (double)fileSize);
    }

    public static final class LeagueFileReader implements Runnable {
        public static final class ProgressEvent extends Event {
            public static final EventType<ProgressEvent> ANY = new EventType<>(Event.ANY, "ANY");
            public static final EventType<ProgressEvent> UPDATE = new EventType<>(ANY, "UPDATE");
            public static final EventType<ProgressEvent> COMPLETE = new EventType<>(ANY, "COMPLETE");

            private final long progress;

            public ProgressEvent(EventType<? extends Event> eventType, long progress) {
                super(eventType);
                this.progress = progress;
            }

            public long getProgress() {
                return this.progress;
            }
        }

        private final MainFX app;
        private final File inputFile;
        private final AtomicStringList dest;

        LeagueFileReader(MainFX app, File inputFile, AtomicStringList dest) {
            this.app = app;
            this.inputFile = inputFile;
            this.dest = dest;
        }

        public void run() {
            final List<String> document = new ArrayList<>();
            long progress = 0;

            try (final BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile.getPath()), StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
/*
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.print("");
                    }
*/
                    document.add(line);
                    progress += line.length() + 1;
                    app.fireEvent(new ProgressEvent(ProgressEvent.UPDATE, progress));
                }
            } catch (IOException e) {
                System.err.println(e);
                return;
            }

            dest.set(document);
            app.fireEvent(new ProgressEvent(ProgressEvent.COMPLETE, progress));
        }
    }
}

package com.hitchhikerprod.league.tasks;

import com.hitchhikerprod.league.League;
import com.hitchhikerprod.league.LeagueFactory;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class ReadLeagueFile extends Task<League> {
    final File inputFile;

    public ReadLeagueFile(File inputFile) {
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
                //sleepHelper(1);
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
        //sleepHelper(500);
        updateTitle("Complete.");
        updateProgress(1, 1);
        return l;
    }
}

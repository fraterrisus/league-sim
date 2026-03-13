package com.hitchhikerprod.league.tasks;

import com.hitchhikerprod.league.definitions.League;
import javafx.concurrent.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class WikiExportLeagueFile extends Task<Void> {
    final League league;
    final File outputFile;

    public WikiExportLeagueFile(League league, File outputFile) {
        this.league = league;
        this.outputFile = outputFile;
    }

    @Override
    public Void call() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(league.exportWiki());
        } catch (Exception e) {
            setException(e);
            throw new RuntimeException(e);
        }
        return null;
    }
}

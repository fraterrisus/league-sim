package com.hitchhikerprod.league.tasks;

import com.hitchhikerprod.league.League;
import com.hitchhikerprod.league.beans.LeagueData;
import javafx.concurrent.Task;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class SaveLeagueFile extends Task<Void> {
    final League league;
    final File outputFile;

    public SaveLeagueFile(League league, File outputFile) {
        this.league = league;
        this.outputFile = outputFile;
    }

    @Override
    public Void call() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            final LeagueData leagueData = league.export();
            final DumperOptions options = new DumperOptions();
            final Yaml emitter = new Yaml(options);
            writer.write(emitter.dumpAs(leagueData, Tag.MAP, DumperOptions.FlowStyle.BLOCK));
        } catch (IOException e) {
            setException(e);
        }
        return null;
    }
}

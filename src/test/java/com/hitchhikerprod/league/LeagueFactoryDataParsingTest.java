package com.hitchhikerprod.league;

import com.hitchhikerprod.league.definitions.League;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class LeagueFactoryDataParsingTest {

    private String getYamlFromResource(String resourceName) {
        final StringBuilder builder = new StringBuilder();
        try (final InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourceName)) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    @Test
    void parseAFL() {
        final String yaml = getYamlFromResource("afl-2025.yml");
        final League league = LeagueFactory.fromYaml(yaml);
    }

    @Test
    void parseUFA() {
        final String yaml = getYamlFromResource("ufa-2025.yml");
        final League league = LeagueFactory.fromYaml(yaml);
    }
}
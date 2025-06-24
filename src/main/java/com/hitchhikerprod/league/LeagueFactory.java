package com.hitchhikerprod.league;

import com.hitchhikerprod.league.beans.LeagueData;
import com.hitchhikerprod.league.definitions.UFA2025;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class LeagueFactory {
    public static League fromYaml(String rawLeagueData) {
        final LoaderOptions loaderOptions = new LoaderOptions();
        final Constructor constructor = new Constructor(LeagueData.class, loaderOptions);

        final Yaml parser = new Yaml(constructor);
        final LeagueData leagueData = parser.load(rawLeagueData);

        return switch (leagueData.league.type) {
            //case "afl" -> new AFL(leagueData);
            case "ufa-2025" -> new UFA2025(leagueData);
            default -> throw new RuntimeException("Unrecognized league format " + leagueData.league.type);
        };
    }
}

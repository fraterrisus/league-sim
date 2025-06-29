package com.hitchhikerprod.league;

import com.hitchhikerprod.league.beans.RawLeagueData;
import com.hitchhikerprod.league.definitions.AFL2025;
import com.hitchhikerprod.league.definitions.UFA2025;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class LeagueFactory {
    public static League fromYaml(String rawLeagueData) {
        final LoaderOptions loaderOptions = new LoaderOptions();
        final Constructor constructor = new Constructor(RawLeagueData.class, loaderOptions);

        final Yaml parser = new Yaml(constructor);
        final RawLeagueData leagueData = parser.load(rawLeagueData);

        return switch (leagueData.league.type) {
            case "afl-2025" -> AFL2025.from(leagueData);
            case "ufa-2025" -> UFA2025.from(leagueData);
            default -> throw new RuntimeException("Unrecognized league format " + leagueData.league.type);
        };
    }
}

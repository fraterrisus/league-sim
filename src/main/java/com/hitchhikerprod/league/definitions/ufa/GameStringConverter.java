package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.util.Converter;

import java.util.Map;

public class GameStringConverter implements Converter<String, UFAGameData> {
    final Map<String, UFATeamData> teams;

    public GameStringConverter(Map<String, UFATeamData> teams) {
        this.teams = teams;
    }

    @Override
    public UFAGameData convert(String that) {
        final String[] tokens = that.split("[,:\\-\\s]+");

        final UFATeamData awayTeam = teams.get(tokens[0]);
        if (awayTeam == null) {
            System.err.println("Unrecognized team ID " + tokens[0]);
            return null;
        }

        final int homeTeamIndex = tokens.length - 1;
        final UFATeamData homeTeam = teams.get(tokens[homeTeamIndex]);
        if (homeTeam == null) {
            System.err.println("Unrecognized team ID " + tokens[3]);
            return null;
        }

        final UFAGameData gameData = new UFAGameData(awayTeam, homeTeam);
        if (homeTeamIndex == 1) return gameData;

        gameData.setAwayScore(Integer.valueOf(tokens[1]));
        gameData.setHomeScore(Integer.valueOf(tokens[2]));
        return gameData;
    }
}

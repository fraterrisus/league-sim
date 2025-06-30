package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.util.Converter;

import java.util.Map;

public class GameStringConverter implements Converter<String, GameData> {
    final Map<String, TeamData> teams;

    public GameStringConverter(Map<String, TeamData> teams) {
        this.teams = teams;
    }

    @Override
    public GameData convert(String that) {
        final String[] tokens = that.split("[,:\\-\\s]+");

        final TeamData awayTeam = teams.get(tokens[0]);
        if (awayTeam == null) {
            System.err.println("Unrecognized team ID " + tokens[0]);
            return null;
        }

        final int homeTeamIndex = tokens.length - 1;
        final TeamData homeTeam = teams.get(tokens[homeTeamIndex]);
        if (homeTeam == null) {
            System.err.println("Unrecognized team ID " + tokens[3]);
            return null;
        }

        final GameData gameData = new GameData(awayTeam, homeTeam);
        if (homeTeamIndex == 1) return gameData;

        gameData.setAwayScore(Integer.valueOf(tokens[1]));
        gameData.setHomeScore(Integer.valueOf(tokens[2]));
        return gameData;
    }
}

package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.util.Converter;

import java.util.Map;
import java.util.Objects;

public class GameMapConverter implements Converter<Map, GameData> {
    final Map<String, TeamData> teams;

    public GameMapConverter(Map<String, TeamData> teams) {
        this.teams = teams;
    }

    private TeamData parseTeamName(Object teamNameObj) throws ClassCastException {
        final String teamName = (String)teamNameObj;
        final TeamData team = teams.get(teamName);
        if (Objects.isNull(team)) {
            throw new RuntimeException("Team " + teamName + " not found");
        }
        return team;
    }
    
    private Integer parseScore(Object scoreObj) throws NumberFormatException {
        if (Objects.isNull(scoreObj)) {
            return null;
        } else if (scoreObj instanceof Integer) {
            return (Integer)scoreObj;
        } else if (scoreObj instanceof String) {
            if (((String) scoreObj).isBlank()) return null;
            return Integer.parseInt(scoreObj.toString());
        } else {
            throw new RuntimeException("Can't parse away score " + scoreObj.getClass().getName());
        }
    }
    
    @Override
    public GameData convert(Map that) {
        TeamData awayTeam = null;
        TeamData homeTeam = null;
        Integer awayScore = null;
        Integer homeScore = null;
        
        for (Object key : that.keySet()) {
            try {
                final String keyName = (String)key;
                switch (keyName) {
                    case "awayTeam" -> awayTeam = parseTeamName(that.get(key));
                    case "homeTeam" -> homeTeam = parseTeamName(that.get(key));
                    case "awayScore" -> awayScore = parseScore(that.get(key));
                    case "homeScore" -> homeScore = parseScore(that.get(key));
                    default -> throw new RuntimeException("Unrecognized score key " + keyName);
                }
            } catch (ClassCastException e) {
                throw new RuntimeException("Couldn't parse types of map", e);
            }
        }

        if (Objects.isNull(awayTeam) && Objects.isNull(homeTeam)) {
            throw new RuntimeException("One or both teams is missing from game");
        }
        GameData game = new GameData(awayTeam, homeTeam);
        game.setAwayScore(awayScore);
        game.setHomeScore(homeScore);
        return game;
    }
}

package com.hitchhikerprod.league.definitions.afl;

import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.util.Converter;

import java.util.Map;
import java.util.Objects;

public class GameRawConverter implements Converter<RawGame, GameData> {
    final Map<String, TeamData> teams;

    public GameRawConverter(Map<String, TeamData> teams) {
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

    @Override
    public GameData convert(RawGame that) {
        final TeamData awayTeam = parseTeamName(that.awayTeam);
        final TeamData homeTeam = parseTeamName(that.homeTeam);
        final GameData gameData = new GameData(awayTeam, homeTeam);

        if (!(that.awayScore instanceof Map) || !(that.homeScore instanceof Map)) {
            throw new ClassCastException();
        }
        final Map<String,Integer> awayScoreMap = (Map<String,Integer>) that.awayScore;
        final Map<String,Integer> homeScoreMap = (Map<String,Integer>) that.homeScore;

        gameData.setAwayGoals(awayScoreMap.get("goals"));
        gameData.setAwayBehinds(awayScoreMap.get("behinds"));
        gameData.setHomeGoals(homeScoreMap.get("goals"));
        gameData.setHomeBehinds(homeScoreMap.get("behinds"));
        return gameData;
    }
}

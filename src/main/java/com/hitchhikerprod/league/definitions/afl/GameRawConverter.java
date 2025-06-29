package com.hitchhikerprod.league.definitions.afl;

import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.util.Converter;

import java.util.Map;
import java.util.Objects;

public class GameRawConverter implements Converter<RawGame, AFLGameData> {
    final Map<String, AFLTeamData> teams;

    public GameRawConverter(Map<String, AFLTeamData> teams) {
        this.teams = teams;
    }

    private AFLTeamData parseTeamName(Object teamNameObj) throws ClassCastException {
        final String teamName = (String)teamNameObj;
        final AFLTeamData team = teams.get(teamName);
        if (Objects.isNull(team)) {
            throw new RuntimeException("Team " + teamName + " not found");
        }
        return team;
    }

    @Override
    public AFLGameData convert(RawGame that) {
        final AFLTeamData awayTeam = parseTeamName(that.awayTeam);
        final AFLTeamData homeTeam = parseTeamName(that.homeTeam);
        final AFLGameData gameData = new AFLGameData(awayTeam, homeTeam);

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

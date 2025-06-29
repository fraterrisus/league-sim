package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.util.Converter;

import java.util.Map;
import java.util.Objects;

public class GameRawConverter implements Converter<RawGame, UFAGameData> {
    final Map<String, UFATeamData> teams;

    public GameRawConverter(Map<String, UFATeamData> teams) {
        this.teams = teams;
    }

    private UFATeamData parseTeamName(Object teamNameObj) throws ClassCastException {
        final String teamName = (String)teamNameObj;
        final UFATeamData team = teams.get(teamName);
        if (Objects.isNull(team)) {
            throw new RuntimeException("Team " + teamName + " not found");
        }
        return team;
    }

    @Override
    public UFAGameData convert(RawGame that) {
        UFATeamData awayTeam = parseTeamName(that.awayTeam);
        UFATeamData homeTeam = parseTeamName(that.homeTeam);
        final UFAGameData gameData = new UFAGameData(awayTeam, homeTeam);
        gameData.setAwayScore((Integer)that.awayScore);
        gameData.setHomeScore((Integer)that.homeScore);
        return gameData;
    }
}

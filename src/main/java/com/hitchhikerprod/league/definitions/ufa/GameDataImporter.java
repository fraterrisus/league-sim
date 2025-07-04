package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.util.Converter;

import java.util.Map;
import java.util.Objects;

public class GameDataImporter implements Converter<RawGame, GameData> {
    final Map<String, TeamData> teams;

    public GameDataImporter(Map<String, TeamData> teams) {
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
        TeamData awayTeam = parseTeamName(that.awayTeam);
        TeamData homeTeam = parseTeamName(that.homeTeam);
        final GameData gameData = new GameData(awayTeam, homeTeam);
        gameData.setAwayScore((Integer)that.awayScore);
        gameData.setHomeScore((Integer)that.homeScore);
        return gameData;
    }
}

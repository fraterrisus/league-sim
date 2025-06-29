package com.hitchhikerprod.league.definitions.afl;

import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.util.Converter;

import java.util.Map;

class GameToRawConverter implements Converter<AFLGameData, RawGame> {
    @Override
    public RawGame convert(AFLGameData that) {
        final RawGame game = new RawGame();
        game.awayTeam = that.getAwayTeam().getId();
        game.awayScore = Map.of("goals", that.getAwayGoals(), "behinds", that.getAwayBehinds());
        game.homeTeam = that.getHomeTeam().getId();
        game.homeScore = Map.of("goals", that.getHomeGoals(), "behinds", that.getHomeBehinds());
        return game;
    }
}

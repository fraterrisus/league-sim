package com.hitchhikerprod.league.definitions.afl;

import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.util.Converter;

import java.util.HashMap;
import java.util.Map;

class GameToRawConverter implements Converter<GameData, RawGame> {
    @Override
    public RawGame convert(GameData that) {
        final RawGame game = new RawGame();
        game.awayTeam = that.getAwayTeam().getId();

        final Map<String, Integer> awayScore = new HashMap<>();
        awayScore.put("goals", that.getAwayGoals());
        awayScore.put("behinds", that.getAwayBehinds());
        game.awayScore = awayScore;

        game.homeTeam = that.getHomeTeam().getId();

        final Map<String, Integer> homeScore = new HashMap<>();
        homeScore.put("goals", that.getHomeGoals());
        homeScore.put("behinds", that.getHomeBehinds());
        game.homeScore = homeScore;

        return game;
    }
}

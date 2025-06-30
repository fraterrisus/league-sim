package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.util.Converter;

public class GameToRawConverter implements Converter<GameData, RawGame> {
    @Override
    public RawGame convert(GameData that) {
        final RawGame game = new RawGame();
        game.awayTeam = that.getAwayTeam().getId();
        game.awayScore = that.getAwayScore();
        game.homeTeam = that.getHomeTeam().getId();
        game.homeScore = that.getHomeScore();
        return game;
    }
}

package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.beans.RawMatchDay;
import com.hitchhikerprod.league.util.Converter;

import java.util.stream.Collectors;

public class MatchDayExporter implements Converter<MatchDay, RawMatchDay> {
    @Override
    public RawMatchDay convert(MatchDay that) {
        final RawMatchDay matchDay = new RawMatchDay();
        matchDay.setName(that.getName());
        matchDay.setGames(that.games.stream().map(g -> {
            final RawGame game = new RawGame();
            game.awayTeam = g.getAwayTeam().getId();
            game.awayScore = g.getAwayScore();
            game.homeTeam = g.getHomeTeam().getId();
            game.homeScore = g.getHomeScore();
            return game;
        }).collect(Collectors.toList()));
        return matchDay;
    }
}

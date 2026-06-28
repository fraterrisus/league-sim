package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.RawMatchDay;
import com.hitchhikerprod.league.util.Converter;

import java.util.stream.Collectors;

public class MatchDayExporter implements Converter<MatchDay, RawMatchDay> {
    @Override
    public RawMatchDay convert(MatchDay that) {
        final GameExporter gameConverter = new GameExporter();

        final RawMatchDay matchDay = new RawMatchDay();
        matchDay.setName(that.getName());
        matchDay.setGames(that.games.stream().map(gameConverter::convert).collect(Collectors.toList()));
        return matchDay;
    }
}

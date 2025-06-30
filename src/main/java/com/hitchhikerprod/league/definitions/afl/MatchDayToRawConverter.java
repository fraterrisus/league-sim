package com.hitchhikerprod.league.definitions.afl;

import com.hitchhikerprod.league.beans.RawMatchDay;
import com.hitchhikerprod.league.util.Converter;

import java.util.stream.Collectors;

class MatchDayToRawConverter implements Converter<MatchDay, RawMatchDay> {
    @Override
    public RawMatchDay convert(MatchDay that) {
        final GameToRawConverter gameConverter = new GameToRawConverter();

        final RawMatchDay matchDay = new RawMatchDay();
        matchDay.setName(that.getName());
        matchDay.setGames(that.games.stream().map(gameConverter::convert).collect(Collectors.toList()));
        return matchDay;
    }
}

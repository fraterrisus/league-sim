package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.beans.RawMatchDay;
import com.hitchhikerprod.league.util.Converter;

import java.util.List;
import java.util.Map;

public class MatchDayImporter implements Converter<RawMatchDay, MatchDay> {
    final Converter<RawGame, GameData> gameConverter;

    public MatchDayImporter(Map<String, TeamData> teams) {
        gameConverter = new GameDataImporter(teams);
    }

    @Override
    public MatchDay convert(RawMatchDay that) {
        final MatchDay matchDay = new MatchDay(that.getName());
        final List<GameData> gameData = that.getGames().stream().map(gameConverter::convert).toList();
        matchDay.setGames(gameData);
        matchDay.setComplete(gameData.stream().allMatch(GameData::isComplete));
        return matchDay;
    }
}

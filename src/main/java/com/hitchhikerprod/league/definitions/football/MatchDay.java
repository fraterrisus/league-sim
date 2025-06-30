package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;

import java.util.ArrayList;
import java.util.List;

public class MatchDay implements LeagueMatchDay {
    private final String name;
    final List<GameData> games;
    private boolean complete;

    public MatchDay(String name) {
        this.name = name;
        this.games = new ArrayList<>();
        this.complete = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<? extends LeagueGameData> getGames() {
        return games;
    }

    public void setGames(List<GameData> games) {
        this.games.clear();
        this.games.addAll(games);
    }

    public void addGame(GameData game) {
        games.add(game);
    }

    @Override
    public boolean isComplete() {
        return complete && !games.isEmpty();
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}

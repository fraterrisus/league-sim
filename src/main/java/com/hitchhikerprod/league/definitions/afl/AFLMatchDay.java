package com.hitchhikerprod.league.definitions.afl;

import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;

import java.util.ArrayList;
import java.util.List;

public class AFLMatchDay implements LeagueMatchDay {
    private final String name;
    final List<AFLGameData> games;
    private boolean complete;

    public AFLMatchDay(String name) {
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

    public void setGames(List<AFLGameData> games) {
        this.games.clear();
        this.games.addAll(games);
    }

    public void addGame(AFLGameData game) {
        games.add(game);
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}

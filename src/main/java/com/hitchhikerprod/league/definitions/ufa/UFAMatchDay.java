package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;

import java.util.ArrayList;
import java.util.List;

public class UFAMatchDay implements LeagueMatchDay {
    private final String name;
    final List<UFAGameData> games;
    private boolean complete;

    public UFAMatchDay(String name) {
        this.name = name;
        this.games = new ArrayList<>();
        this.complete = false;
    }

    public String getName() {
        return name;
    }

    public List<? extends LeagueGameData> getGames() {
        return games;
    }

    public void setGames(List<UFAGameData> games) {
        this.games.clear();
        this.games.addAll(games);
    }

    public void addGame(UFAGameData game) {
        games.add(game);
    }

    public boolean isComplete() {
        return complete && !games.isEmpty();
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}

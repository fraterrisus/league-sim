package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class MatchDay implements LeagueMatchDay {
    private final String name;
    final ObservableList<GameData> games;
    private boolean complete;

    public MatchDay(String name) {
        this.name = name;
        this.games = FXCollections.observableArrayList();
        this.complete = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ObservableList<? extends LeagueGameData> getGames() {
        return games;
    }

    public void setGames(List<? extends LeagueGameData> games) throws ClassCastException {
        this.games.clear();
        if (games.isEmpty()) return;
        for (LeagueGameData game : games) {
            this.games.add((GameData) game);
        }
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

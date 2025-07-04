package com.hitchhikerprod.league.beans;

import javafx.collections.ObservableList;

import java.util.List;

public interface LeagueMatchDay {
    String getName();
    ObservableList<? extends LeagueGameData> getGames();
    void setGames(List<? extends LeagueGameData> l);
    boolean isComplete();
    void setComplete(boolean b);
}

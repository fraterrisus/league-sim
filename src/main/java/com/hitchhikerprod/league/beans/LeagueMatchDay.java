package com.hitchhikerprod.league.beans;

import java.util.List;

public interface LeagueMatchDay {
    String getName();
    List<? extends LeagueGameData> getGames();
    void setGames(List<? extends LeagueGameData> l);
    boolean isComplete();
    void setComplete(boolean b);
}

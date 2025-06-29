package com.hitchhikerprod.league.beans;

import java.util.List;

public interface LeagueMatchDay {
    String getName();
    List<? extends LeagueGameData> getGames();
}

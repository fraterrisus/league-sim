package com.hitchhikerprod.league.beans;

import javafx.beans.property.SimpleObjectProperty;

import java.util.List;

public interface LeagueGameData {
    LeagueTeamData getAwayTeam();
    LeagueTeamData getHomeTeam();
    List<SimpleObjectProperty<Integer>> getAwayScoreProperties();
    List<SimpleObjectProperty<Integer>> getHomeScoreProperties();

    default boolean hasTeam(LeagueTeamData team) {
        return (getAwayTeam() == team || getHomeTeam() == team);
    }
}

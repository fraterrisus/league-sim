package com.hitchhikerprod.league.beans;

import javafx.collections.ObservableList;

import java.util.List;

public interface LeagueDivision extends Named {
    List<String> getTeams();
    ObservableList<? extends LeagueTeamData> getObservableTeams();
}

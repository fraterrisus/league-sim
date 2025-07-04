package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.LeagueDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class Division implements LeagueDivision {
    private final String name;
    private final ObservableList<TeamData> teams;

    public Division(String name) {
        this.name = name;
        this.teams = FXCollections.observableArrayList();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getTeams() {
        return teams.stream().map(TeamData::getName).collect(Collectors.toList());
    }

    public void setTeams(List<TeamData> newTeams) {
        teams.clear();
        teams.addAll(newTeams);
    }

    @Override
    public ObservableList<TeamData> getObservableTeams() {
        return teams;
    }
}

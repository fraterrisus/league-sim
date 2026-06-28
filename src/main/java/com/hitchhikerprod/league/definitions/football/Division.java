package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.LeagueDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class Division implements LeagueDivision {
    private final String name;
    private final ObservableList<TeamData> teams;
    private final boolean isDynamic;

    public Division(String name, boolean isDynamic) {
        this.name = name;
        this.teams = FXCollections.observableArrayList();
        this.isDynamic = isDynamic;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDynamic() {
        return isDynamic;
    }

    @Override
    public List<String> getTeams() {
        return teams.stream().map(TeamData::getId).collect(Collectors.toList());
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

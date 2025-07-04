package com.hitchhikerprod.league.beans;

import java.util.ArrayList;
import java.util.List;

public class RawDivision implements LeagueDivision {
    private String name;
    private List<String> teams;

    public static RawDivision of(String name) {
        final RawDivision newDiv = new RawDivision();
        newDiv.setName(name);
        newDiv.setTeams(new ArrayList<>());
        return newDiv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTeams() {
        return teams;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
    }
}

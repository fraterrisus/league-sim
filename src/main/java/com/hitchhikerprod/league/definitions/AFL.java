package com.hitchhikerprod.league.definitions;

import com.hitchhikerprod.league.League;
import com.hitchhikerprod.league.beans.Division;
import com.hitchhikerprod.league.beans.LeagueData;

import java.util.List;
import java.util.Map;

public class AFL implements League {

    public AFL(LeagueData leagueData) {
    }

    @Override
    public Map<Division, List<UFA2025.TeamData>> getDivisionTables() {
        return Map.of();
    }

    @Override
    public String tables() {
        return "";
    }
}

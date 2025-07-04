package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.RawDivision;
import com.hitchhikerprod.league.util.Converter;

import java.util.Map;
import java.util.Objects;

public class DivisionImporter implements Converter<RawDivision, Division> {
    private final Map<String, TeamData> teams;

    public DivisionImporter(Map<String, TeamData> teams) {
        this.teams = teams;
    }

    @Override
    public Division convert(RawDivision that) {
        final Division div = new Division(that.getName());
        div.setTeams(that.getTeams().stream()
                .map(teamId -> Objects.requireNonNull(teams.get(teamId), "Division references missing team " + teamId))
                .toList());
        return div;
    }
}

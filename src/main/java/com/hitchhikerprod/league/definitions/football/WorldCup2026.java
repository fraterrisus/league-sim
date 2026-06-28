package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.RawDivision;
import com.hitchhikerprod.league.beans.RawLeagueData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorldCup2026 extends FootballGroupStage {
    public static final String LEAGUE_TYPE = "world-cup-2026";

    public WorldCup2026(Map<String, TeamData> teams, RawLeagueData leagueData, List<Division> divisions,
                        List<MatchDay> matchDays) {
        super(teams, leagueData, divisions, matchDays, WorldCupTeamComparator::new);
    }

    public static WorldCup2026 from(RawLeagueData leagueData) {
        final Map<String, TeamData> teams = leagueData.teams.stream()
                .map(t -> new TeamData(t.getName(), t.getId()))
                .collect(Collectors.toMap(TeamData::getId, t -> t));

        final GameImporter gameImporter = new GameImporter(teams);
        final List<MatchDay> matchDays = leagueData.matchdays.stream().map(md -> {
            final MatchDay matchDay = new MatchDay(md.getName());
            final List<GameData> gameData = md.getGames().stream().map(gameImporter::convert).toList();
            matchDay.setGames(gameData);
            matchDay.setComplete(gameData.stream().allMatch(GameData::isComplete));
            return matchDay;
        }).collect(Collectors.toList());

        final DivisionImporter divisionImporter = new DivisionImporter(teams);
        final List<Division> divisions = new ArrayList<>();
        for (RawDivision div : leagueData.divisions) {
            divisions.add(divisionImporter.convert(div));
        }
        divisions.add(new WildcardDivision("Third Place"));

        return new WorldCup2026(teams, leagueData, divisions, matchDays);
    }

    public static class WildcardDivision extends Division {
        public WildcardDivision(String name) {
            super(name, true);
        }
    }
}

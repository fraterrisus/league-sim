package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.RawLeagueData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GoldCup2025 extends FootballGroupStage {
    public static final String LEAGUE_TYPE = "gold-cup-2025";

    public GoldCup2025(Map<String, TeamData> teams, RawLeagueData leagueData, List<Division> divisions,
                       List<MatchDay> matchDays) {
        super(teams, leagueData, divisions, matchDays, GoldCupTeamComparator::new);
    }

    public static GoldCup2025 from(RawLeagueData leagueData) {
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
        final List<Division> divisions = leagueData.divisions.stream()
                .map(divisionImporter::convert).toList();

        return new GoldCup2025(teams, leagueData, divisions, matchDays);
    }
}

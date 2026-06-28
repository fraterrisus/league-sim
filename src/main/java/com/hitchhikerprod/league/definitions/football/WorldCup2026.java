package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.LeagueDivision;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.beans.RawDivision;
import com.hitchhikerprod.league.beans.RawLeagueData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        divisions.add(new ThirdPlaceDivision());

        return new WorldCup2026(teams, leagueData, divisions, matchDays);
    }

    public static class ThirdPlaceDivision extends Division {
        public ThirdPlaceDivision() {
            super("Third Place", true);
        }

        public void pickTeams(Map<Division, List<TeamData>> staticDivisions) {
            setTeams(staticDivisions.values().stream()
                    .map(teamData -> teamData.get(2))
                    .toList());
        }
    }

    @Override
    protected Map<Division, List<? extends LeagueTeamData>> rankDivisions() {
        final Map<Boolean, List<Division>> divisionMap = divisions.stream()
                .collect(Collectors.partitioningBy(LeagueDivision::isDynamic));
        final Map<Division, List<TeamData>> rankedDivisions = divisionMap.get(false).stream()
                .collect(Collectors.toMap(div -> div, div -> rankTeams(div.getTeams())));
        for (Division div : divisionMap.get(true)) {
            if (div instanceof ThirdPlaceDivision tpd) {
                tpd.pickTeams(rankedDivisions);
                rankedDivisions.put(div, rankTeams(div.getTeams()));
            }
        }
        return rankedDivisions.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<TeamData> rankTeams(List<String> teamsIn) {
        final Comparator<TeamData> tc = new WorldCupTeamComparator(matchDays);

        final List<TeamData> teams = new ArrayList<>();
        for (String shortName : teamsIn) {
            final TeamData team = this.teams.get(shortName);
            if (Objects.isNull(team)) {
                throw new RuntimeException("Team '" + shortName + "' does not exist");
            }
            teams.add(team);
        }

        return teams.stream().sorted(tc).toList().reversed();
    }
}

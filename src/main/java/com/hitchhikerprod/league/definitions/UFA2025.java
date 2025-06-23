package com.hitchhikerprod.league.definitions;

import com.hitchhikerprod.league.League;
import com.hitchhikerprod.league.beans.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UFA2025 implements League {
    private static final Map<String, Double> SCORE_MAP = null;

    public static class TeamData {
        String shortName;
        String fullName;
        int wins = 0;
        int losses = 0;
        int goalDifference = 0;
        String lastGame;
        String nextGame;

        public String getFullName() {
            return fullName;
        }

        public Integer getWins() {
            return wins;
        }

        public Integer getLosses() {
            return losses;
        }

        public Double getWinPercentage() {
            return (double)wins / (double)(wins + losses);
        }

        public Integer getGoalDifference() {
            return goalDifference;
        }
    }

    private final Map<String, TeamData> teams;
    private final LeagueData leagueData;

    public UFA2025(LeagueData leagueData) {
        this.leagueData = leagueData;
        this.teams = new HashMap<>();

        for (Team team : leagueData.teams) {
            TeamData teamData = new TeamData();
            teamData.shortName = team.id;
            teamData.fullName = team.name;
            teams.put(team.id, teamData);
        }

        for (MatchDay md : leagueData.matchdays) {
            for (Game g : md.games) {
                final TeamData awayData = teams.get(g.awayTeam);
                if (awayData == null) {
                    System.err.println("Unrecognized team ID " + g.awayTeam);
                    continue;
                }
                final TeamData homeData = teams.get(g.homeTeam);
                if (homeData == null) {
                    System.err.println("Unrecognized team ID " + g.homeTeam);
                    continue;
                }
                final Double awayValue = g.getAwayValue(SCORE_MAP);
                final Double homeValue = g.getHomeValue(SCORE_MAP);

                if (awayValue == null || homeValue == null) {
                    if (awayData.nextGame == null) {
                        awayData.nextGame = "at " + g.homeTeam;
                    }
                    if (homeData.nextGame == null) {
                        homeData.nextGame = "vs " + g.awayTeam;
                    }
                    continue;
                }

                final int awayScore = awayValue.intValue();
                final int homeScore = homeValue.intValue();

                final String lastGame = String.format("%s %2d-%2d %s",
                        g.awayTeam, awayScore, homeScore, g.homeTeam);
                awayData.lastGame = lastGame;
                homeData.lastGame = lastGame;

                if (awayScore > homeScore) {
                    awayData.wins++;
                    homeData.losses++;
                } else {
                    awayData.losses++;
                    homeData.wins++;
                }
                awayData.goalDifference += awayScore - homeScore;
                homeData.goalDifference += homeScore - awayScore;
            }
        }
    }

    @Override
    public Map<Division, List<TeamData>> getDivisionTables() {
        return leagueData.divisions.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        div -> rankTeams(div.teams)
                ));
    }

    @Override
    public String tables() {
        StringBuilder sb = new StringBuilder();
        for (Division div : leagueData.divisions) {
            sb.append(String.format("%-32s", div.name));
            sb.append("  W  L   Pct  +/-\n");

            for (TeamData team : rankTeams(div.teams)) {
                sb.append("  ").append(String.format("%-30s", team.fullName));
                sb.append(" ").append(String.format("%2d", team.wins));
                sb.append(" ").append(String.format("%2d", team.losses));
                final int played = team.wins + team.losses;
                final double winPercent = (played == 0) ? 0d : (double)team.wins / (double)played;
                sb.append(" ").append(String.format("%5.3f", winPercent));
                sb.append(" ").append(String.format("%+4d", team.goalDifference));
                if (team.lastGame != null) {
                    sb.append("  Last: ").append(team.lastGame);
                }
                if (team.nextGame != null) {
                    sb.append("  Next: ").append(team.nextGame);
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private class TeamComparator implements Comparator<TeamData> {
        @Override
        public int compare(TeamData t1, TeamData t2) {
            // #1: overall win percentage
            final int t1Played = t1.wins + t1.losses;
            final double t1Percent = (t1Played == 0) ? 0d : (double)t1.wins / (double)t1Played;
            final int t2Played = t2.wins + t2.losses;
            final double t2Percent = (t2Played == 0) ? 0d : (double)t2.wins / (double)t2Played;
            final int c = Double.compare(t1Percent, t2Percent);
            if (c != 0) return c;

            // #2: head-to-head wins
            int t1HeadWins = 0;
            int t2HeadWins = 0;
            for (MatchDay md : leagueData.matchdays) {
                for (Game g : md.games) {
                    if (g.awayTeam == null) continue;
                    if (g.homeTeam == null) continue;
                    if (g.getAwayValue(SCORE_MAP) == null) continue;
                    if (g.getHomeValue(SCORE_MAP) == null) continue;

                    if (t1.shortName.compareTo(g.awayTeam) == 0 && t2.shortName.compareTo(g.homeTeam) == 0) {
                        if (g.getAwayValue(SCORE_MAP) > g.getHomeValue(SCORE_MAP)) t1HeadWins++;
                        else t2HeadWins++;
                    } else if (t1.shortName.compareTo(g.homeTeam) == 0 && t2.shortName.compareTo(g.awayTeam) == 0) {
                        if (g.getHomeValue(SCORE_MAP) > g.getAwayValue(SCORE_MAP)) t1HeadWins++;
                        else t2HeadWins++;
                    }
                }
            }
            return Integer.compare(t1HeadWins, t2HeadWins);
        }
    }

    private List<TeamData> rankTeams(List<String> teamsIn) {
        final TeamComparator tc = new TeamComparator();

        return teamsIn.stream()
                .map(teams::get)
                .sorted(tc)
                .toList()
                .reversed();
    }
}

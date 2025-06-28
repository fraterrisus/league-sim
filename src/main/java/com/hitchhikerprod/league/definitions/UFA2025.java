package com.hitchhikerprod.league.definitions;

import com.hitchhikerprod.league.League;
import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.beans.RawDivision;
import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.beans.RawLeagueData;
import com.hitchhikerprod.league.beans.RawMatchDay;
import com.hitchhikerprod.league.beans.RawTeamData;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UFA2025 implements League {
    private static final Map<String, Double> SCORE_MAP = null;

    public static class MatchDay {
        private final String name;
        private final List<UFAGameData> games;
        private boolean complete;

        public MatchDay(String name) {
            this.name = name;
            this.games = new ArrayList<>();
            this.complete = false;
        }

        public String getName() {
            return name;
        }

        public List<UFAGameData> getGames() {
            return games;
        }

        public void addGame(UFAGameData game) {
            games.add(game);
        }

        public boolean isComplete() {
            return complete;
        }

        public void setComplete(boolean complete) {
            this.complete = complete;
        }
    }

    public static class UFAGameData implements LeagueGameData {
        private final TeamData awayTeam;
        private final TeamData homeTeam;
        private final SimpleObjectProperty<Integer> awayScore;
        private final SimpleObjectProperty<Integer> homeScore;

        public UFAGameData(TeamData awayTeam, TeamData homeTeam) {
            this.awayTeam = awayTeam;
            this.homeTeam = homeTeam;
            this.awayScore = new SimpleObjectProperty<>(null);
            this.homeScore = new SimpleObjectProperty<>(null);
        }

        public TeamData getAwayTeam() {
            return awayTeam;
        }

        public TeamData getHomeTeam() {
            return homeTeam;
        }

        public Integer getAwayScore() {
            return awayScore.getValue();
        }

        public List<SimpleObjectProperty<Integer>> getAwayScoreProperties() {
            return List.of(awayScore);
        }

        public void setAwayScore(Integer newScore) {
            awayScore.set(newScore);
        }
        
        public Integer getHomeScore() {
            return homeScore.getValue();
        }

        public void setHomeScore(Integer newScore) {
            homeScore.set(newScore);
        }

        public List<SimpleObjectProperty<Integer>> getHomeScoreProperties() {
            return List.of(homeScore);
        }
    }

    public static class TeamData implements LeagueTeamData {
        final String shortName;
        final String fullName;
        int wins = 0;
        int losses = 0;
        int goalDifference = 0;

        public TeamData(String fullName, String shortName) {
            this.fullName = fullName;
            this.shortName = shortName;
        }

        public void reset() {
            wins = 0;
            losses = 0;
            goalDifference = 0;
        }

        public String getId() {
            return shortName;
        }

        public String getName() {
            return fullName;
        }

        public Integer getWins() {
            return wins;
        }

        public Integer getLosses() {
            return losses;
        }

        public Double getWinPercentage() {
            if (wins + losses == 0) return 0.0;
            return (double)wins / (double)(wins + losses);
        }

        public Integer getGoalDifference() {
            return goalDifference;
        }
    }

    private final Map<String, TeamData> teams;
    private final RawLeagueData leagueData;
    private final List<MatchDay> matchDays;

    public UFA2025(RawLeagueData leagueData) {
        this.leagueData = leagueData;
        this.teams = new HashMap<>();
        this.matchDays = new ArrayList<>();

        for (RawTeamData team : leagueData.teams) {
            TeamData teamData = new TeamData(team.getName(), team.getId());
            teams.put(team.getId(), teamData);
        }

        for (RawMatchDay md : leagueData.matchdays) {
            final MatchDay matchDay = new MatchDay(md.getName());
            matchDays.add(matchDay);
            matchDay.setComplete(true);

            for (RawGame g : md.getGames()) {
                final TeamData awayTeam = teams.get(g.awayTeam);
                if (awayTeam == null) {
                    System.err.println("Unrecognized team ID " + g.awayTeam);
                    continue;
                }

                final TeamData homeTeam = teams.get(g.homeTeam);
                if (homeTeam == null) {
                    System.err.println("Unrecognized team ID " + g.homeTeam);
                    continue;
                }

                final UFAGameData gameData = new UFAGameData(awayTeam, homeTeam);
                matchDay.addGame(gameData);

                final Double awayValue = g.getAwayValue(SCORE_MAP);
                final Double homeValue = g.getHomeValue(SCORE_MAP);

                if (awayValue == null || homeValue == null) {
                    matchDay.setComplete(false);
                    continue;
                }

                gameData.setAwayScore(awayValue.intValue());
                gameData.setHomeScore(homeValue.intValue());
            }
        }
    }

    @Override
    public RawLeagueData export() {
        final RawLeagueData doc = new RawLeagueData();
        doc.league = this.leagueData.league;
        doc.teams = this.leagueData.teams;
        doc.divisions = this.leagueData.divisions;
        doc.matchdays = this.matchDays.stream().map(md -> {
            final RawMatchDay matchDay = new RawMatchDay();
            matchDay.setName(md.name);
            matchDay.setGames(md.getGames().stream().map(g -> {
                final RawGame rawGame = new RawGame();
                rawGame.awayTeam = g.getAwayTeam().getId();
                rawGame.awayScore = g.getAwayScore();
                rawGame.homeTeam = g.getHomeTeam().getId();
                rawGame.homeScore = g.getHomeScore();
                return rawGame;
            }).collect(Collectors.toList()));
            return matchDay;
        }).collect(Collectors.toList());
        return doc;
    }

    @Override
    public int getLatestCompleteMatchDay() {
        for (int idx = 0; idx < matchDays.size(); idx++) {
            MatchDay matchDay = matchDays.get(idx);
            if (!matchDay.isComplete()) return idx - 1;
        }
        return matchDays.size() - 1;
    }

    @Override
    public List<String> getMatchDays() {
        return matchDays.stream().map(MatchDay::getName).collect(Collectors.toList());
    }

    @Override
    public List<? extends LeagueGameData> getGames(int matchDay) {
        return matchDays.get(matchDay).getGames();
    }

    @Override
    public Map<RawDivision, List<TeamData>> getDivisionTables(int matchDayIndex) {
        teams.values().forEach(TeamData::reset);

        for (int idx = 0; idx <= matchDayIndex; idx++) {
            final MatchDay matchDay = matchDays.get(idx);
            for (UFAGameData game : matchDay.getGames()) {
                if (game.getAwayScore() == null || game.getHomeScore() == null) continue;
                if (game.getAwayScore() > game.getHomeScore()) {
                    game.getAwayTeam().wins++;
                    game.getHomeTeam().losses++;
                } else {
                    game.getAwayTeam().losses++;
                    game.getHomeTeam().wins++;
                }
                game.getAwayTeam().goalDifference += game.getAwayScore() - game.getHomeScore();
                game.getHomeTeam().goalDifference += game.getHomeScore() - game.getAwayScore();
            }
        }

        return leagueData.divisions.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        div -> rankTeams(div.teams)
                ));
    }

    private List<TeamData> rankTeams(List<String> teamsIn) {
        final TeamComparator tc = new TeamComparator();

        return teamsIn.stream()
                .map(teams::get)
                .sorted(tc)
                .toList()
                .reversed();
    }

    private class TeamComparator implements Comparator<TeamData> {
        @Override
        public int compare(TeamData t1, TeamData t2) {
            // #1: overall win percentage
            final int c = Double.compare(t1.getWinPercentage(), t2.getWinPercentage());
            if (c != 0) return c;

            // #2: head-to-head wins
            int t1HeadWins = 0;
            int t2HeadWins = 0;
            for (MatchDay md : matchDays) {
                for (UFAGameData g : md.getGames()) {
                    if (g.getAwayScore() == null || g.getHomeScore() == null) continue;
                    if (t1 == g.getAwayTeam() && t2 == g.getHomeTeam()) {
                        if (g.getAwayScore() > g.getHomeScore()) t1HeadWins++;
                        else t2HeadWins++;
                    } else if (t1 == g.getHomeTeam() && t2 == g.getAwayTeam()) {
                        if (g.getHomeScore() > g.getAwayScore()) t1HeadWins++;
                        else t2HeadWins++;
                    }
                }
            }
            return Integer.compare(t1HeadWins, t2HeadWins);
        }

    }
}

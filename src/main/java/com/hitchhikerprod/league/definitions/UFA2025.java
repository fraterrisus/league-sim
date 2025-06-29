package com.hitchhikerprod.league.definitions;

import com.hitchhikerprod.league.League;
import com.hitchhikerprod.league.beans.LeagueColumn;
import com.hitchhikerprod.league.beans.LeagueDivision;
import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.beans.RawLeagueData;
import com.hitchhikerprod.league.beans.RawMatchDay;
import com.hitchhikerprod.league.beans.RawTeamData;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UFA2025 implements League {
    private static final Map<String, Double> SCORE_MAP = null;

    private final Map<String, UFATeamData> teams;
    private final RawLeagueData leagueData;
    private final List<UFAMatchDay> matchDays;

    public UFA2025(RawLeagueData leagueData) {
        this.leagueData = leagueData;
        this.teams = new HashMap<>();
        this.matchDays = new ArrayList<>();

        for (RawTeamData team : leagueData.teams) {
            UFATeamData teamData = new UFATeamData(team.getName(), team.getId());
            teams.put(team.getId(), teamData);
        }

        for (RawMatchDay md : leagueData.matchdays) {
            final UFAMatchDay matchDay = new UFAMatchDay(md.getName());
            matchDays.add(matchDay);
            matchDay.setComplete(true);

            for (RawGame g : md.getGames()) {
                final UFATeamData awayTeam = teams.get(g.awayTeam);
                if (awayTeam == null) {
                    System.err.println("Unrecognized team ID " + g.awayTeam);
                    continue;
                }

                final UFATeamData homeTeam = teams.get(g.homeTeam);
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
            matchDay.setGames(md.games.stream().map(g -> {
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
            UFAMatchDay matchDay = matchDays.get(idx);
            if (!matchDay.isComplete()) return idx - 1;
        }
        return matchDays.size() - 1;
    }

    @Override
    public List<String> getMatchDays() {
        return matchDays.stream().map(UFAMatchDay::getName).toList();
    }

    @Override
    public List<LeagueMatchDay> getGames(LeagueTeamData team) {
        final List<LeagueMatchDay> result = new ArrayList<>();
        for (UFAMatchDay matchDay : matchDays) {
            final List<UFAGameData> filteredGames = matchDay.games.stream()
                    .filter(g -> g.hasTeam(team))
                    .toList();
            if (!filteredGames.isEmpty()) {
                final UFAMatchDay filteredMatchDay = new UFAMatchDay(matchDay.getName());
                filteredMatchDay.setGames(filteredGames);
                result.add(filteredMatchDay);
            }
        }
        return result;
    }

    @Override
    public List<? extends LeagueGameData> getGames(int matchDay) {
        return matchDays.get(matchDay).getGames();
    }

    @Override
    public List<LeagueColumn<?>> getDivisionColumns() {
        return List.of(
                new LeagueColumn<>(0, Integer.class, "W", Pos.CENTER),
                new LeagueColumn<>(1, Integer.class, "L", Pos.CENTER),
                new LeagueColumn<>(2, Double.class, "Pct", Pos.CENTER_RIGHT, "%5.3f"),
                new LeagueColumn<>(3, Integer.class, "+/-", Pos.CENTER_RIGHT)
        );
    }

    @Override
    public Map<? extends LeagueDivision, List<? extends LeagueTeamData>> getDivisionTables(int matchDayIndex) {
        teams.values().forEach(UFATeamData::reset);

        for (int idx = 0; idx <= matchDayIndex; idx++) {
            final UFAMatchDay matchDay = matchDays.get(idx);
            for (UFAGameData game : matchDay.games) {
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
                        div -> div,
                        div -> rankTeams(div.getTeams())
                ));
    }

    private List<UFATeamData> rankTeams(List<String> teamsIn) {
        final TeamComparator tc = new TeamComparator();

        return teamsIn.stream()
                .map(teams::get)
                .sorted(tc)
                .toList()
                .reversed();
    }

    private class TeamComparator implements Comparator<UFATeamData> {
        @Override
        public int compare(UFATeamData t1, UFATeamData t2) {
            // #1: overall win percentage
            final int c = Double.compare(t1.getWinPercentage(), t2.getWinPercentage());
            if (c != 0) return c;

            // #2: head-to-head wins
            int t1HeadWins = 0;
            int t2HeadWins = 0;
            for (UFAMatchDay md : matchDays) {
                for (UFAGameData g : md.games) {
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

    public static class UFAMatchDay implements LeagueMatchDay {
        private final String name;
        private final List<UFAGameData> games;
        private boolean complete;

        public UFAMatchDay(String name) {
            this.name = name;
            this.games = new ArrayList<>();
            this.complete = false;
        }

        public String getName() {
            return name;
        }

        public List<? extends LeagueGameData> getGames() {
            return games;
        }

        public void setGames(List<UFAGameData> games) {
            this.games.clear();
            this.games.addAll(games);
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
        private final UFATeamData awayTeam;
        private final UFATeamData homeTeam;
        private final SimpleObjectProperty<Integer> awayScore;
        private final SimpleObjectProperty<Integer> homeScore;

        public UFAGameData(UFATeamData awayTeam, UFATeamData homeTeam) {
            this.awayTeam = awayTeam;
            this.homeTeam = homeTeam;
            this.awayScore = new SimpleObjectProperty<>(null);
            this.homeScore = new SimpleObjectProperty<>(null);
        }

        public UFATeamData getAwayTeam() {
            return awayTeam;
        }

        public UFATeamData getHomeTeam() {
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

    public static class UFATeamData implements LeagueTeamData {
        final String shortName;
        final String fullName;
        int wins = 0;
        int losses = 0;
        int goalDifference = 0;

        public UFATeamData(String fullName, String shortName) {
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

        public <T> T getData(Class<T> klass, int index) {
            try {
                return klass.cast(switch(index) {
                    case 0 -> getWins();
                    case 1 -> getLosses();
                    case 2 -> getWinPercentage();
                    case 3 -> getGoalDifference();
                    default -> throw new ArrayIndexOutOfBoundsException();
                });
            } catch (ClassCastException e) {
                return null;
            }
        }
    }
}

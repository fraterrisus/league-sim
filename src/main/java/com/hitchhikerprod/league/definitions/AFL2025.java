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
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AFL2025 implements League {
    private static final Map<String, Double> SCORE_MAP = Map.of("goals", 6.0, "behinds", 1.0);

    private final Map<String, AFLTeamData> teams;
    private final RawLeagueData leagueData;
    private final List<AFLMatchDay> matchDays;

    private AFL2025(Map<String, AFLTeamData> teams, RawLeagueData leagueData, List<AFLMatchDay> matchDays) {
        this.teams = teams;
        this.leagueData = leagueData;
        this.matchDays = matchDays;
    }

    public static AFL2025 from(RawLeagueData leagueData) {
        final Map<String, AFLTeamData> teams = leagueData.teams.stream()
                .map(t -> new AFLTeamData(t.getId(), t.getName()))
                .collect(Collectors.toMap(AFLTeamData::getId, t -> t));

        final List<AFLMatchDay> matchDays = new ArrayList<>();
        for (RawMatchDay md : leagueData.matchdays) {
            final AFLMatchDay matchDay = new AFLMatchDay(md.getName());
            matchDays.add(matchDay);
            matchDay.setComplete(true);

            for (RawGame g : md.getGames()) {
                final AFLTeamData awayTeam = teams.get(g.awayTeam);
                if (awayTeam == null) {
                    System.err.println("Unrecognized team ID " + g.awayTeam);
                    continue;
                }

                final AFLTeamData homeTeam = teams.get(g.homeTeam);
                if (homeTeam == null) {
                    System.err.println("Unrecognized team ID " + g.homeTeam);
                    continue;
                }

                final AFLGameData gameData = new AFLGameData(awayTeam, homeTeam);
                matchDay.addGame(gameData);

                if (!(g.awayScore instanceof Map) || !(g.homeScore instanceof Map)) {
                    throw new ClassCastException();
                }
                final Map<String,Integer> awayScoreMap = (Map<String,Integer>) g.awayScore;
                final Integer awayGoals = awayScoreMap.get("goals");
                final Integer awayBehinds = awayScoreMap.get("behinds");
                final Map<String,Integer> homeScoreMap = (Map<String,Integer>) g.homeScore;
                final Integer homeGoals = homeScoreMap.get("goals");
                final Integer homeBehinds = homeScoreMap.get("behinds");

                if (Objects.isNull(awayGoals) || Objects.isNull(awayBehinds) || Objects.isNull(homeGoals) || Objects.isNull(homeBehinds)) {
                    matchDay.setComplete(false);
                    continue;
                }

                gameData.awayGoals.setValue(awayGoals);
                gameData.awayBehinds.setValue(awayBehinds);
                gameData.homeGoals.setValue(homeGoals);
                gameData.homeBehinds.setValue(homeBehinds);
            }
        }

        return new AFL2025(teams, leagueData, matchDays);
    }

    @Override
    public RawLeagueData export() {
        return null;
    }

    @Override
    public int getLatestCompleteMatchDay() {
        for (int idx = 0; idx < matchDays.size(); idx++) {
            AFLMatchDay matchDay = matchDays.get(idx);
            if (!matchDay.isComplete()) return idx - 1;
        }
        return matchDays.size() - 1;
    }

    @Override
    public List<String> getMatchDays() {
        return matchDays.stream().map(AFLMatchDay::getName).toList();
    }

    @Override
    public List<LeagueMatchDay> getGames(LeagueTeamData teamData) {
        final List<LeagueMatchDay> result = new ArrayList<>();
        for (AFLMatchDay matchDay : matchDays) {
            final List<AFLGameData> filteredGames = matchDay.games.stream()
                    .filter(g -> g.hasTeam(teamData))
                    .toList();
            if (!filteredGames.isEmpty()) {
                final AFLMatchDay filteredMatchDay = new AFLMatchDay(matchDay.getName());
                filteredMatchDay.setGames(filteredGames);
                result.add(filteredMatchDay);
            }
        }
        return result;
    }

    @Override
    public List<? extends LeagueGameData> getGames(int matchDayIndex) {
        return matchDays.get(matchDayIndex).getGames();
    }

    @Override
    public List<LeagueColumn<?>> getDivisionColumns() {
        return List.of(
                new LeagueColumn<>(0, Integer.class, "Pts", Pos.CENTER),
                new LeagueColumn<>(1, Double.class, "%", Pos.CENTER_RIGHT, "%5.1f"),
                new LeagueColumn<>(2, Integer.class, "W", Pos.CENTER),
                new LeagueColumn<>(3, Integer.class, "L", Pos.CENTER),
                new LeagueColumn<>(4, Integer.class, "D", Pos.CENTER),
                new LeagueColumn<>(5, Integer.class, "PF", Pos.CENTER),
                new LeagueColumn<>(6, Integer.class, "PA", Pos.CENTER)
        );
    }

    @Override
    public Map<? extends LeagueDivision, List<? extends LeagueTeamData>> getDivisionTables(int matchDayIndex) {
        teams.values().forEach(AFLTeamData::reset);

        for (int idx = 0; idx <= matchDayIndex; idx++) {
            final AFLMatchDay matchDay = matchDays.get(idx);
            for (AFLGameData game: matchDay.games) {
                if (game.getAwayScore() == null || game.getHomeScore() == null) continue;
                int c = Integer.compare(game.getAwayScore(), game.getHomeScore());
                if (c > 0) {
                    game.getAwayTeam().wins++;
                    game.getHomeTeam().losses++;
                } else if (c == 0) {
                    game.getAwayTeam().draws++;
                    game.getHomeTeam().draws++;
                } else {
                    game.getAwayTeam().losses++;
                    game.getHomeTeam().wins++;
                }
                game.getAwayTeam().pointsFor += game.getAwayScore();
                game.getAwayTeam().pointsAgainst += game.getHomeScore();
                game.getHomeTeam().pointsFor += game.getHomeScore();
                game.getHomeTeam().pointsAgainst += game.getAwayScore();
            }
        }

        return leagueData.divisions.stream()
                .collect(Collectors.toMap(
                        div -> div,
                        div -> rankTeams(div.getTeams())
                ));
    }

    private List<AFLTeamData> rankTeams(List<String> teamsIn) {
        final TeamComparator tc = new TeamComparator();
        return teamsIn.stream()
                .map(this.teams::get)
                .sorted(tc)
                .toList()
                .reversed();
    }

    private static class TeamComparator implements Comparator<AFLTeamData> {
        @Override
        public int compare(AFLTeamData o1, AFLTeamData o2) {
            final int c = Integer.compare(o1.getPoints(), o2.getPoints());
            if (c != 0) return c;

            return Double.compare(o1.getPercentage(), o2.getPercentage());
        }
    }

    public static class AFLMatchDay implements LeagueMatchDay {
        private final String name;
        private final List<AFLGameData> games;
        private boolean complete;

        public AFLMatchDay(String name) {
            this.name = name;
            this.games = new ArrayList<>();
            this.complete = false;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<? extends LeagueGameData> getGames() {
            return games;
        }

        public void setGames(List<AFLGameData> games) {
            this.games.clear();
            this.games.addAll(games);
        }

        public void addGame(AFLGameData game) {
            games.add(game);
        }

        public boolean isComplete() {
            return complete;
        }

        public void setComplete(boolean complete) {
            this.complete = complete;
        }
    }

    public static class AFLGameData implements LeagueGameData {
        private final AFLTeamData awayTeam;
        private final AFLTeamData homeTeam;
        private final SimpleObjectProperty<Integer> awayGoals;
        private final SimpleObjectProperty<Integer> awayBehinds;
        private final SimpleObjectProperty<Integer> homeGoals;
        private final SimpleObjectProperty<Integer> homeBehinds;

        public AFLGameData(AFLTeamData awayTeam, AFLTeamData homeTeam) {
            this.awayTeam = awayTeam;
            this.homeTeam = homeTeam;
            this.awayGoals = new SimpleObjectProperty<>(null);
            this.awayBehinds = new SimpleObjectProperty<>(null);
            this.homeGoals = new SimpleObjectProperty<>(null);
            this.homeBehinds = new SimpleObjectProperty<>(null);
        }

        @Override
        public AFLTeamData getAwayTeam() {
            return awayTeam;
        }

        @Override
        public AFLTeamData getHomeTeam() {
            return homeTeam;
        }

        public Integer getAwayScore() {
            return (6 * awayGoals.getValue()) + awayBehinds.getValue();
        }

        @Override
        public List<SimpleObjectProperty<Integer>> getAwayScoreProperties() {
            return List.of(awayGoals, awayBehinds);
        }

        public Integer getHomeScore() {
            return (6 * homeGoals.getValue()) + homeBehinds.getValue();
        }

        @Override
        public List<SimpleObjectProperty<Integer>> getHomeScoreProperties() {
            return List.of(homeGoals, homeBehinds);
        }
    }

    public static class AFLTeamData implements LeagueTeamData {
        private final String id;
        private final String name;

        int wins;
        int draws;
        int losses;
        int pointsFor;
        int pointsAgainst;

        public AFLTeamData(String id, String name) {
            this.id = id;
            this.name = name;
        }

        private void reset() {
            wins = 0;
            draws = 0;
            losses = 0;
            pointsFor = 0;
            pointsAgainst = 0;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getId() {
            return this.id;
        }

        public int getWins() {
            return wins;
        }

        public int getDraws() {
            return draws;
        }

        public int getLosses() {
            return losses;
        }

        public int getPointsFor() {
            return pointsFor;
        }

        public int getPointsAgainst() {
            return pointsAgainst;
        }

        public Integer getPoints() {
            return (4 * this.wins) + (2 * this.draws);
        }

        public Double getPercentage() {
            return 100.0 * (double)pointsFor / (double)pointsAgainst;
        }

        @Override
        public <T> T getData(Class<T> klass, int index) {
            try {
                return klass.cast(switch(index) {
                    case 0 -> getPoints();
                    case 1 -> getPercentage();
                    case 2 -> getWins();
                    case 3 -> getLosses();
                    case 4 -> getDraws();
                    case 5 -> getPointsFor();
                    case 6 -> getPointsAgainst();
                    default -> throw new ArrayIndexOutOfBoundsException();
                });
            } catch (ClassCastException e) {
                return null;
            }
        }
    }
}

package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.League;
import com.hitchhikerprod.league.beans.LeagueColumn;
import com.hitchhikerprod.league.beans.LeagueDivision;
import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.beans.RawLeagueData;
import com.hitchhikerprod.league.beans.RawMatchDay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UFA2025 implements League {
    private final Map<String, UFATeamData> teams;
    private final RawLeagueData leagueData;
    private final List<UFAMatchDay> matchDays;

    private UFA2025(Map<String, UFATeamData> teams, RawLeagueData leagueData, List<UFAMatchDay> matchDays) {
        this.teams = teams;
        this.leagueData = leagueData;
        this.matchDays = matchDays;
    }

    public static UFA2025 from(RawLeagueData leagueData) {
        final Map<String, UFATeamData> teams = leagueData.teams.stream()
                .map(t -> new UFATeamData(t.getName(), t.getId()))
                .collect(Collectors.toMap(UFATeamData::getId, t -> t));

        final List<UFAMatchDay> matchDays = new ArrayList<>();
        for (RawMatchDay md : leagueData.matchdays) {
            final UFAMatchDay matchDay = new UFAMatchDay(md.getName());
            matchDays.add(matchDay);
            matchDay.setComplete(true);

            for (Object game : md.getGames()) {
                final UFAGameData gameData = switch (game) {
                    case String s -> new GameStringConverter(teams).convert(s);
                    case Map map -> new GameMapConverter(teams).convert(map);
                    case RawGame rawGame -> new GameRawConverter(teams).convert(rawGame);
                    case null, default -> throw new RuntimeException("Can't parse game of unknown type");
                };

                matchDay.addGame(gameData);
                if (!gameData.isComplete()) matchDay.setComplete(false);
            }
        }

        return new UFA2025(teams, leagueData, matchDays);
    }

    @Override
    public RawLeagueData export() {
        final RawLeagueData doc = new RawLeagueData();
        doc.league = this.leagueData.league;
        doc.teams = this.leagueData.teams;
        doc.divisions = this.leagueData.divisions;
        doc.matchdays = this.matchDays.stream().map(md -> {
            final RawMatchDay matchDay = new RawMatchDay();
            matchDay.setName(md.getName());
            matchDay.setGames(md.games.stream().map(g -> {
                final RawGame game = new RawGame();
                game.awayTeam = g.getAwayTeam().getId();
                game.awayScore = g.getAwayScore();
                game.homeTeam = g.getHomeTeam().getId();
                game.homeScore = g.getHomeScore();
                return game;
            }).collect(Collectors.toList()));
            return matchDay;
        }).toList();
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
    public List<LeagueMatchDay> getGames(LeagueTeamData teamData) {
        final List<LeagueMatchDay> result = new ArrayList<>();
        for (UFAMatchDay matchDay : matchDays) {
            final List<UFAGameData> filteredGames = matchDay.games.stream()
                    .filter(g -> g.hasTeam(teamData))
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
    public List<? extends LeagueGameData> getGames(int matchDayIndex) {
        return matchDays.get(matchDayIndex).getGames();
    }

    @Override // boilerplate, but references package-private variable COLUMNS
    public List<LeagueColumn<?>> getDivisionColumns() {
        return IntStream.range(0, UFATeamData.COLUMNS.size())
                .mapToObj(idx -> UFATeamData.COLUMNS.get(idx).toColumn(idx))
                .collect(Collectors.toUnmodifiableList());
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
        final TeamComparator tc = new TeamComparator(this.matchDays);
        return teamsIn.stream()
                .map(this.teams::get)
                .sorted(tc)
                .toList()
                .reversed();
    }
}

package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.LeagueColumn;
import com.hitchhikerprod.league.beans.LeagueDivision;
import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.beans.RawLeagueData;
import com.hitchhikerprod.league.definitions.League;
import com.hitchhikerprod.league.definitions.LeagueUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UFA2025 implements League {
    private final Map<String, TeamData> teams;
    private final RawLeagueData leagueData;
    private final ObservableList<MatchDay> matchDays;

    private UFA2025(Map<String, TeamData> teams, RawLeagueData leagueData, List<MatchDay> matchDays) {
        this.teams = teams;
        this.leagueData = leagueData;
        this.matchDays = FXCollections.observableList(matchDays);
    }

    public static UFA2025 from(RawLeagueData leagueData) {
        return LeagueUtils.newLeagueFrom(
                leagueData,
                t -> new TeamData(t.getName(), t.getId()),
                GameRawConverter::new,
                MatchDay::new,
                UFA2025::new
        );
/*
        for (RawMatchDay md : leagueData.matchdays) {
            for (Object game : md.getGames()) {
                final GameData gameData = switch (game) {
                    case String s -> new GameStringConverter(teams).convert(s);
                    case Map map -> new GameMapConverter(teams).convert(map);
                    case RawGame rawGame -> new GameRawConverter(teams).convert(rawGame);
                    case null, default -> throw new RuntimeException("Can't parse game of unknown type");
                };
            }
        }
*/
    }

    @Override
    public RawLeagueData export() {
        final MatchDayToRawConverter matchDayConverter = new MatchDayToRawConverter();

        final RawLeagueData doc = new RawLeagueData();
        doc.league = this.leagueData.league;
        doc.teams = this.leagueData.teams;
        doc.divisions = this.leagueData.divisions;
        doc.matchdays = this.matchDays.stream().map(matchDayConverter::convert).toList();
        return doc;
    }

    @Override
    public int getLatestCompleteMatchDay() {
        return LeagueUtils.getLatestCompleteMatchDay(matchDays);
    }

    @Override
    public ObservableList<? extends LeagueMatchDay> getMatchDays() {
        return matchDays;
    }

    @Override
    public void addMatchDay(int index, String name) {
        matchDays.add(index, new MatchDay(name));
    }

    @Override
    public void addMatchDay(String name) {
        matchDays.add(new MatchDay(name));
    }

    @Override
    public List<LeagueMatchDay> getGames(LeagueTeamData teamData) {
        final List<LeagueMatchDay> result = new ArrayList<>();
        for (MatchDay matchDay : matchDays) {
            final List<GameData> filteredGames = matchDay.games.stream()
                    .filter(g -> g.hasTeam(teamData))
                    .toList();
            if (!filteredGames.isEmpty()) {
                final MatchDay filteredMatchDay = new MatchDay(matchDay.getName());
                filteredMatchDay.setGames(filteredGames);
                result.add(filteredMatchDay);
            }
        }
        return result;
    }

    @Override
    public List<? extends LeagueGameData> getGames(int matchDayIndex) {
        return LeagueUtils.getGames(matchDayIndex, matchDays);
    }

    @Override
    public void createGame(int matchDayIndex, String awayTeamId, String homeTeamId) {
        if (matchDayIndex < 0) return;
        final MatchDay matchDay = matchDays.get(matchDayIndex);
        final TeamData awayTeam = teams.get(awayTeamId);
        final TeamData homeTeam = teams.get(homeTeamId);
        final GameData game = new GameData(awayTeam, homeTeam);
        matchDay.addGame(game);
    }

    @Override
    public List<? extends LeagueTeamData> getTeams() {
        return leagueData.teams;
    }

    @Override
    public List<LeagueColumn<?>> getDivisionColumns() {
        return LeagueUtils.getDivisionColumns(TeamData.COLUMNS);
    }

    @Override
    public Map<? extends LeagueDivision, List<? extends LeagueTeamData>> getDivisionTables(int matchDayIndex) {
        teams.values().forEach(TeamData::reset);

        for (int idx = 0; idx <= matchDayIndex; idx++) {
            final MatchDay matchDay = matchDays.get(idx);
            for (GameData game : matchDay.games) {
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

    private List<TeamData> rankTeams(List<String> teamsIn) {
        final TeamComparator tc = new TeamComparator(this.matchDays);
        return teamsIn.stream()
                .map(this.teams::get)
                .sorted(tc)
                .toList()
                .reversed();
    }
}

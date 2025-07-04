package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.LeagueColumn;
import com.hitchhikerprod.league.beans.LeagueDivision;
import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.beans.RawDivision;
import com.hitchhikerprod.league.beans.RawLeague;
import com.hitchhikerprod.league.beans.RawLeagueData;
import com.hitchhikerprod.league.beans.RawMatchDay;
import com.hitchhikerprod.league.beans.RawTeamData;
import com.hitchhikerprod.league.definitions.League;
import com.hitchhikerprod.league.definitions.LeagueUtils;
import com.hitchhikerprod.league.util.Converter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UFA2025 implements League {
    public static final String LEAGUE_TYPE = "ufa-2025";

    private final String leagueName;
    private final ObservableList<Division> divisions;
    private final ObservableList<MatchDay> matchDays;

    private UFA2025(String leagueName, List<Division> divisions, List<MatchDay> matchDays) {
        this.leagueName = leagueName;
        // observableArrayList is a copy constructor that creates a new ArrayList as a backing store for the ObsList
        this.divisions = FXCollections.observableArrayList(divisions);
        this.matchDays = FXCollections.observableArrayList(matchDays);
    }

    public static UFA2025 from(RawLeagueData leagueData) {
        final Converter<RawTeamData, TeamData> teamImporter = t -> new TeamData(t.getName(), t.getId());
        final Map<String, TeamData> importedTeams = leagueData.teams.stream()
                .map(teamImporter::convert)
                .collect(Collectors.toMap(TeamData::getId, t1 -> t1));

        final Converter<RawDivision, Division> divisionImporter = new DivisionImporter(importedTeams);
        final List<Division> importedDivisions = leagueData.divisions.stream()
                .map(divisionImporter::convert).toList();

        final Converter<RawMatchDay, MatchDay> matchDayImporter = new MatchDayImporter(importedTeams);
        final List<MatchDay> importedMatchDays = leagueData.matchdays.stream()
                .map(matchDayImporter::convert).toList();

        return new UFA2025(leagueData.league.name, importedDivisions, importedMatchDays);
    }

    /*  You can `switch` on classes now:
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

    @Override
    public RawLeagueData export() {
        final Converter<Division, Stream<RawTeamData>> teamExporter = new TeamExporter();
        final Converter<Division, RawDivision> divisionExporter = new DivisionExporter();
        final Converter<MatchDay, RawMatchDay> matchDayExporter = new MatchDayExporter();

        final RawLeagueData doc = new RawLeagueData();
        doc.league = RawLeague.from(this.leagueName, LEAGUE_TYPE);
        doc.teams = this.divisions.stream().flatMap(teamExporter::convert)
                .sorted(Comparator.comparing(RawTeamData::getName)).toList();
        doc.divisions = this.divisions.stream().map(divisionExporter::convert).toList();
        doc.matchdays = this.matchDays.stream().map(matchDayExporter::convert).toList();
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
    public ObservableList<? extends LeagueGameData> getGames(int matchDayIndex) {
        return LeagueUtils.getGames(matchDayIndex, matchDays);
    }

    @Override
    public void createGame(int matchDayIndex, String awayTeamId, String homeTeamId) {
        if (matchDayIndex < 0) return;
        final MatchDay matchDay = matchDays.get(matchDayIndex);
        final TeamData awayTeam = getTeamById(awayTeamId);
        final TeamData homeTeam = getTeamById(homeTeamId);
        final GameData game = new GameData(awayTeam, homeTeam);
        matchDay.addGame(game);
    }

    @Override
    public List<? extends LeagueTeamData> getTeams() {
        return divisions.stream().flatMap(div -> div.getObservableTeams().stream()).toList();
    }

    @Override
    public ObservableList<? extends LeagueDivision> getDivisions() {
        return divisions;
    }

    @Override
    public void addDivision(int index, String name) {
        divisions.add(index, new Division(name));
    }

    @Override
    public void addDivision(String name) {
        divisions.add(new Division(name));
    }

    @Override
    public List<LeagueColumn<?>> getDivisionColumns() {
        return LeagueUtils.getDivisionColumns(TeamData.COLUMNS);
    }

    @Override
    public Map<? extends LeagueDivision, List<? extends LeagueTeamData>> getDivisionTables(int matchDayIndex) {
        resetTeamData();

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

        return divisions.stream().collect(Collectors.toMap(div -> div, div -> rankTeams(div.getObservableTeams())));
    }

    private TeamData getTeamById(String teamId) {
        return divisions.stream()
                .flatMap(div -> div.getObservableTeams().stream())
                .filter(team -> team.getId().equals(teamId))
                .findFirst()
                .orElseThrow();
    }

    private void resetTeamData() {
        divisions.stream()
                .flatMap(div -> div.getObservableTeams().stream())
                .forEach(TeamData::reset);
    }

    private List<TeamData> rankTeams(List<TeamData> teamsIn) {
        final TeamComparator tc = new TeamComparator(this.matchDays);
        return teamsIn.stream()
                .sorted(tc)
                .toList()
                .reversed();
    }
}

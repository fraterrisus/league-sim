package com.hitchhikerprod.league.definitions.afl;

import com.hitchhikerprod.league.League;
import com.hitchhikerprod.league.beans.LeagueColumn;
import com.hitchhikerprod.league.beans.LeagueDivision;
import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.beans.RawLeagueData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AFL2025 implements League {
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

        final List<AFLMatchDay> matchDays = leagueData.matchdays.stream().map(md -> {
            final AFLMatchDay matchDay = new AFLMatchDay(md.getName());
            final GameRawConverter rawConverter = new GameRawConverter(teams);
            final List<AFLGameData> gameData = md.getGames().stream().map(rawConverter::convert).toList();
            matchDay.setGames(gameData);
            matchDay.setComplete(gameData.stream().noneMatch(game ->
                    Objects.isNull(game.awayBehinds) ||
                    Objects.isNull(game.awayGoals) ||
                    Objects.isNull(game.homeBehinds) ||
                    Objects.isNull(game.homeGoals)));
            return matchDay;
        }).toList();

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
        if (matchDayIndex < 0) { return List.of(); }
        return matchDays.get(matchDayIndex).getGames();
    }

    @Override // boilerplate, but references package-private variable COLUMNS
    public List<LeagueColumn<?>> getDivisionColumns() {
        return IntStream.range(0, AFLTeamData.COLUMNS.size())
                .mapToObj(idx -> AFLTeamData.COLUMNS.get(idx).toColumn(idx))
                .collect(Collectors.toUnmodifiableList());
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
}

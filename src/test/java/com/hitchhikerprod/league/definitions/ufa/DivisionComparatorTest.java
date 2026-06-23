package com.hitchhikerprod.league.definitions.ufa;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DivisionComparatorTest {
    @Test
    void noTieBreakers() {
        final List<TeamData> teams = getTeams();

        /* Results:
         * Team 0: 3-0
         * Team 2: 2-1
         * Team 1: 1-2
         * Team 3: 0-3
         */
        final MatchDay md1 = new MatchDay("Day 1");
        md1.addGame(getGameData(teams.get(2), 1, 2, teams.get(0)));
        md1.addGame(getGameData(teams.get(3), 1, 2, teams.get(1)));
        final MatchDay md2 = new MatchDay("Day 2");
        md2.addGame(getGameData(teams.get(1), 1, 2, teams.get(0)));
        md2.addGame(getGameData(teams.get(3), 1, 2, teams.get(2)));
        final MatchDay md3 = new MatchDay("Day 3");
        md3.addGame(getGameData(teams.get(3), 1, 2, teams.get(0)));
        md3.addGame(getGameData(teams.get(1), 1, 2, teams.get(2)));
        final ObservableList<MatchDay> matchDays = FXCollections.observableArrayList(List.of(md1, md2, md3));

        final DivisionComparator uut = new DivisionComparator(matchDays, teams);
        final List<TeamData> result = uut.rank().reversed();

        assertEquals(teams.get(0), result.get(0)); // 3-0
        assertEquals(teams.get(2), result.get(1)); // 2-1
        assertEquals(teams.get(1), result.get(2)); // 1-2
        assertEquals(teams.get(3), result.get(3)); // 0-3
    }

    @Test
    void headToHead() {
        final List<TeamData> teams = getTeams();

        /* Results:
         * Team 1: 2-1, beat T3 on md2
         * Team 3: 2-1
         * Team 2: 1-2, beat T0 on md2
         * Team 0: 1-2
         */
        final MatchDay md1 = new MatchDay("Day 1");
        md1.addGame(getGameData(teams.get(2), 1, 2, teams.get(1)));
        md1.addGame(getGameData(teams.get(0), 1, 2, teams.get(3)));
        final MatchDay md2 = new MatchDay("Day 2");
        md2.addGame(getGameData(teams.get(3), 1, 2, teams.get(1)));
        md2.addGame(getGameData(teams.get(0), 1, 2, teams.get(2)));
        final MatchDay md3 = new MatchDay("Day 3");
        md3.addGame(getGameData(teams.get(0), 2, 1, teams.get(1)));
        md3.addGame(getGameData(teams.get(3), 2, 1, teams.get(2)));
        final ObservableList<MatchDay> matchDays = FXCollections.observableArrayList(List.of(md1, md2, md3));

        final DivisionComparator uut = new DivisionComparator(matchDays, teams);
        final List<TeamData> result = uut.rank().reversed();

        assertEquals(teams.get(1), result.get(0));
        assertEquals(teams.get(3), result.get(1));
        assertEquals(teams.get(2), result.get(2));
        assertEquals(teams.get(0), result.get(3));
    }

    private List<TeamData> getTeams() {
        return List.of(
                new TeamData("Boston Beaneaters", "BOS"),
                new TeamData("Chicago Hot Dogs", "CHI"),
                new TeamData("Philly Cheesesteaks", "PHI"),
                new TeamData("Austin Barbecue", "ATX")
        );
    }

    private GameData getGameData(TeamData awayTeam, int awayScore, int homeScore, TeamData homeTeam) {
        final GameData gd = new GameData(awayTeam, homeTeam);
        gd.setAwayScore(awayScore);
        gd.setHomeScore(homeScore);
        return gd;
    }
}
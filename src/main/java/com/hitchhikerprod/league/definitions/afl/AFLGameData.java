package com.hitchhikerprod.league.definitions.afl;

import com.hitchhikerprod.league.beans.LeagueGameData;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;

public class AFLGameData implements LeagueGameData {
    private final AFLTeamData awayTeam;
    private final AFLTeamData homeTeam;
    final SimpleObjectProperty<Integer> awayGoals;
    final SimpleObjectProperty<Integer> awayBehinds;
    final SimpleObjectProperty<Integer> homeGoals;
    final SimpleObjectProperty<Integer> homeBehinds;

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

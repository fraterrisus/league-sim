package com.hitchhikerprod.league.definitions.afl;

import com.hitchhikerprod.league.beans.LeagueGameData;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.Objects;

public class AFLGameData implements LeagueGameData {
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

    public boolean isComplete() {
        return (Objects.nonNull(awayGoals.getValue()) &&
                Objects.nonNull(homeGoals.getValue()) &&
                Objects.nonNull(awayBehinds.getValue()) &&
                Objects.nonNull(homeBehinds.getValue()));
    }

    public Integer getAwayScore() {
        return (6 * awayGoals.getValue()) + awayBehinds.getValue();
    }
    
    public Integer getAwayGoals() {
        return awayGoals.getValue();
    }
    
    public Integer getAwayBehinds() {
        return awayBehinds.getValue();
    }

    @Override
    public List<SimpleObjectProperty<Integer>> getAwayScoreProperties() {
        return List.of(awayGoals, awayBehinds);
    }

    public Integer getHomeScore() {
        return (6 * homeGoals.getValue()) + homeBehinds.getValue();
    }

    public Integer getHomeGoals() {
        return homeGoals.getValue();
    }

    public Integer getHomeBehinds() {
        return homeBehinds.getValue();
    }

    @Override
    public List<SimpleObjectProperty<Integer>> getHomeScoreProperties() {
        return List.of(homeGoals, homeBehinds);
    }
}

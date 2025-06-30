package com.hitchhikerprod.league.definitions.afl;

import com.hitchhikerprod.league.beans.LeagueGameData;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.Objects;

public class GameData implements LeagueGameData {
    private final TeamData awayTeam;
    private final TeamData homeTeam;
    private final SimpleObjectProperty<Integer> awayGoals;
    private final SimpleObjectProperty<Integer> awayBehinds;
    private final SimpleObjectProperty<Integer> homeGoals;
    private final SimpleObjectProperty<Integer> homeBehinds;

    public GameData(TeamData awayTeam, TeamData homeTeam) {
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.awayGoals = new SimpleObjectProperty<>(null);
        this.awayBehinds = new SimpleObjectProperty<>(null);
        this.homeGoals = new SimpleObjectProperty<>(null);
        this.homeBehinds = new SimpleObjectProperty<>(null);
    }

    @Override
    public TeamData getAwayTeam() {
        return awayTeam;
    }

    @Override
    public TeamData getHomeTeam() {
        return homeTeam;
    }

    public boolean isComplete() {
        return (Objects.nonNull(awayGoals.getValue()) &&
                Objects.nonNull(homeGoals.getValue()) &&
                Objects.nonNull(awayBehinds.getValue()) &&
                Objects.nonNull(homeBehinds.getValue()));
    }

    public Integer getAwayScore() {
        final Integer goals = awayGoals.getValue();
        final Integer behinds = awayBehinds.getValue();
        if (Objects.isNull(goals) || Objects.isNull(behinds)) return null;
        return (6 * goals) + behinds;
    }
    
    public Integer getAwayGoals() {
        return awayGoals.getValue();
    }
    
    public Integer getAwayBehinds() {
        return awayBehinds.getValue();
    }

    public void setAwayGoals(Integer goals) {
        awayGoals.set(goals);
    }

    public void setAwayBehinds(Integer behinds) {
        awayBehinds.set(behinds);
    }

    @Override
    public List<SimpleObjectProperty<Integer>> getAwayScoreProperties() {
        return List.of(awayGoals, awayBehinds);
    }

    public Integer getHomeScore() {
        final Integer goals = homeGoals.getValue();
        final Integer behinds = homeBehinds.getValue();
        if (Objects.isNull(goals) || Objects.isNull(behinds)) return null;
        return (6 * goals) + behinds;
    }

    public Integer getHomeGoals() {
        return homeGoals.getValue();
    }

    public Integer getHomeBehinds() {
        return homeBehinds.getValue();
    }

    public void setHomeGoals(Integer goals) {
        homeGoals.set(goals);
    }

    public void setHomeBehinds(Integer behinds) {
        homeBehinds.set(behinds);
    }

    @Override
    public List<SimpleObjectProperty<Integer>> getHomeScoreProperties() {
        return List.of(homeGoals, homeBehinds);
    }
}

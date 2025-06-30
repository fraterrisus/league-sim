package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.LeagueGameData;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.Objects;

public class GameData implements LeagueGameData {
    private final TeamData awayTeam;
    private final TeamData homeTeam;
    private final SimpleObjectProperty<Integer> awayGoals;
    private final SimpleObjectProperty<Integer> homeGoals;

    public GameData(TeamData awayTeam, TeamData homeTeam) {
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.awayGoals = new SimpleObjectProperty<>(null);
        this.homeGoals = new SimpleObjectProperty<>(null);
    }

    public boolean isComplete() {
        return (Objects.nonNull(awayGoals.getValue()) && Objects.nonNull(homeGoals.getValue()));
    }

    @Override
    public TeamData getAwayTeam() {
        return awayTeam;
    }

    @Override
    public TeamData getHomeTeam() {
        return homeTeam;
    }

    @Override
    public Integer getAwayScore() {
        return awayGoals.getValue();
    }

    public void setAwayScore(Integer awayScore) {
        awayGoals.setValue(awayScore);
    }

    @Override
    public Integer getHomeScore() {
        return homeGoals.getValue();
    }

    public void setHomeScore(Integer homeScore) {
        homeGoals.setValue(homeScore);
    }

    @Override
    public List<SimpleObjectProperty<Integer>> getAwayScoreProperties() {
        return List.of(awayGoals);
    }

    @Override
    public List<SimpleObjectProperty<Integer>> getHomeScoreProperties() {
        return List.of(homeGoals);
    }
}

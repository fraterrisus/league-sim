package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.LeagueGameData;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.Objects;

public class GameData implements LeagueGameData {
    private final TeamData awayTeam;
    private final TeamData homeTeam;
    private final SimpleObjectProperty<Integer> awayScore;
    private final SimpleObjectProperty<Integer> homeScore;

    public GameData(TeamData awayTeam, TeamData homeTeam) {
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.awayScore = new SimpleObjectProperty<>(null);
        this.homeScore = new SimpleObjectProperty<>(null);
    }

    public boolean isComplete() {
        return (Objects.nonNull(awayScore.getValue()) && Objects.nonNull(homeScore.getValue()));
    }

    @Override
    public TeamData getAwayTeam() {
        return awayTeam;
    }

    @Override
    public TeamData getHomeTeam() {
        return homeTeam;
    }

    public Integer getAwayScore() {
        return awayScore.getValue();
    }

    public List<SimpleObjectProperty<Integer>> getAwayScoreProperties() {
        return List.of(awayScore);
    }

    public void setAwayScore(Integer newScore) {
        awayScore.set(newScore);
    }

    public Integer getHomeScore() {
        return homeScore.getValue();
    }

    public void setHomeScore(Integer newScore) {
        homeScore.set(newScore);
    }

    public List<SimpleObjectProperty<Integer>> getHomeScoreProperties() {
        return List.of(homeScore);
    }
}

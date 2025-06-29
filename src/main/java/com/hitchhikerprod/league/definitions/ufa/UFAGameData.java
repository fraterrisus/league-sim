package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.LeagueGameData;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;

public class UFAGameData implements LeagueGameData {
    private final UFATeamData awayTeam;
    private final UFATeamData homeTeam;
    private final SimpleObjectProperty<Integer> awayScore;
    private final SimpleObjectProperty<Integer> homeScore;

    public UFAGameData(UFATeamData awayTeam, UFATeamData homeTeam) {
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.awayScore = new SimpleObjectProperty<>(null);
        this.homeScore = new SimpleObjectProperty<>(null);
    }

    @Override
    public UFATeamData getAwayTeam() {
        return awayTeam;
    }

    @Override
    public UFATeamData getHomeTeam() {
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

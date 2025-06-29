package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.util.ColumnDef;
import javafx.geometry.Pos;

import java.util.List;
import java.util.Optional;

public class UFATeamData implements LeagueTeamData {
    final String shortName;
    final String fullName;
    int wins = 0;
    int losses = 0;
    int goalDifference = 0;

    public UFATeamData(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    void reset() {
        wins = 0;
        losses = 0;
        goalDifference = 0;
    }

    public String getId() {
        return shortName;
    }

    public String getName() {
        return fullName;
    }

    public Integer getWins() {
        return wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public Double getWinPercentage() {
        if (wins + losses == 0) return 0.0;
        return (double) wins / (double) (wins + losses);
    }

    public Integer getGoalDifference() {
        return goalDifference;
    }

    public <T> T getData(Class<T> klass, int index) {
        try {
            return klass.cast(COLUMNS.get(index).getter().apply(this));
        } catch (ClassCastException e) {
            return null;
        }
    }

    static final List<ColumnDef<UFATeamData,?>> COLUMNS = List.of(
            new ColumnDef<>(Integer.class, "W", Pos.CENTER, Optional.empty(), UFATeamData::getWins),
            new ColumnDef<>(Integer.class, "L", Pos.CENTER, Optional.empty(), UFATeamData::getLosses),
            new ColumnDef<>(Double.class, "Pct", Pos.CENTER_RIGHT, Optional.of("%5.3f"), UFATeamData::getWinPercentage),
            new ColumnDef<>(Integer.class, "+/-", Pos.CENTER_RIGHT, Optional.empty(), UFATeamData::getGoalDifference)
    );
}

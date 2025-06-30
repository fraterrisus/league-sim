package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.util.ColumnDef;
import javafx.geometry.Pos;

import java.util.List;
import java.util.Optional;

public class TeamData implements LeagueTeamData {
    final String shortName;
    final String fullName;
    int wins = 0;
    int draws = 0;
    int losses = 0;
    int goalsFor = 0;
    int goalsAgainst = 0;

    public TeamData(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public void reset() {
        wins = 0;
        draws = 0;
        losses = 0;
        goalsFor = 0;
        goalsAgainst = 0;
    }

    public <T> T getData(Class<T> klass, int index) {
        try {
            return klass.cast(COLUMNS.get(index).getter().apply(this));
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    public String getName() {
        return fullName;
    }

    @Override
    public String getId() {
        return shortName;
    }

    public int getPlayed() {
        return wins + draws + losses;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLosses() {
        return losses;
    }

    public int getPoints() {
        return  (3 * wins) + draws;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    static final List<ColumnDef<TeamData,?>> COLUMNS = List.of(
            new ColumnDef<>(Integer.class, "P", Pos.CENTER, Optional.empty(), TeamData::getPlayed),
            new ColumnDef<>(Integer.class, "W", Pos.CENTER, Optional.empty(), TeamData::getWins),
            new ColumnDef<>(Integer.class, "D", Pos.CENTER, Optional.empty(), TeamData::getDraws),
            new ColumnDef<>(Integer.class, "L", Pos.CENTER, Optional.empty(), TeamData::getLosses),
            new ColumnDef<>(Integer.class, "Pts", Pos.CENTER, Optional.empty(), TeamData::getPoints),
            new ColumnDef<>(Integer.class, "GF", Pos.CENTER, Optional.empty(), TeamData::getGoalsFor),
            new ColumnDef<>(Integer.class, "GA", Pos.CENTER, Optional.empty(), TeamData::getGoalsAgainst),
            new ColumnDef<>(Integer.class, "FD", Pos.CENTER, Optional.empty(), TeamData::getGoalDifference)
    );
}

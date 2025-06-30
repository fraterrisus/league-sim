package com.hitchhikerprod.league.definitions.afl;

import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.util.ColumnDef;
import javafx.geometry.Pos;

import java.util.List;
import java.util.Optional;

public class TeamData implements LeagueTeamData {
    private final String id;
    private final String name;

    int wins;
    int draws;
    int losses;
    int pointsFor;
    int pointsAgainst;

    public TeamData(String id, String name) {
        this.id = id;
        this.name = name;
    }

    void reset() {
        wins = 0;
        draws = 0;
        losses = 0;
        pointsFor = 0;
        pointsAgainst = 0;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getId() {
        return this.id;
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

    public int getPointsFor() {
        return pointsFor;
    }

    public int getPointsAgainst() {
        return pointsAgainst;
    }

    public Integer getPoints() {
        return (4 * this.wins) + (2 * this.draws);
    }

    public Double getPercentage() {
        if (pointsAgainst == 0) return 0.0;
        return 100.0 * (double) pointsFor / (double) pointsAgainst;
    }

    @Override // boilerplate, but references package-private COLUMNS
    public <T> T getData(Class<T> klass, int index) {
        try {
            return klass.cast(COLUMNS.get(index).getter().apply(this));
        } catch (ClassCastException e) {
            return null;
        }
    }

    static final List<ColumnDef<TeamData, ?>> COLUMNS = List.of(
            new ColumnDef<>(Integer.class, "Pts", Pos.CENTER, Optional.empty(), TeamData::getPoints),
            new ColumnDef<>(Double.class, "%", Pos.CENTER_RIGHT, Optional.of("%5.1f"), TeamData::getPercentage),
            new ColumnDef<>(Integer.class, "W", Pos.CENTER, Optional.empty(), TeamData::getWins),
            new ColumnDef<>(Integer.class, "L", Pos.CENTER, Optional.empty(), TeamData::getLosses),
            new ColumnDef<>(Integer.class, "D", Pos.CENTER, Optional.empty(), TeamData::getDraws),
            new ColumnDef<>(Integer.class, "PF", Pos.CENTER, Optional.empty(), TeamData::getPointsFor),
            new ColumnDef<>(Integer.class, "PA", Pos.CENTER, Optional.empty(), TeamData::getPointsAgainst)
    );
}

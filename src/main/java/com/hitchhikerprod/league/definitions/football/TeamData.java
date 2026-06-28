package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.util.ColumnDef;
import javafx.geometry.Pos;
import javafx.util.Pair;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TeamData that)) return false;
        return this.fullName.equalsIgnoreCase(that.fullName);
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
            new ColumnDef<>(Integer.class, "GD", Pos.CENTER, Optional.empty(), TeamData::getGoalDifference)
    );

    protected record HeadToHeadData(int t1Points, int t2Points, int t1Goals, int t2Goals, int t1Diff, int t2Diff) {}

    protected static HeadToHeadData getHeadToHeadData(Iterable<MatchDay> matchDays, TeamData t1, TeamData t2) {
        // Note that, in theory, FIFA breaks three-team ties by including all teams in the "head-to-head" count,
        // so this method doesn't quite work because we can only compare two teams at a time.
        int t1Points = 0;
        int t2Points = 0;
        int t1Goals = 0;
        int t2Goals = 0;
        int t1Diff = 0;
        int t2Diff = 0;
        for (MatchDay md : matchDays) {
            for (GameData g : md.games) {
                if (g.getAwayScore() == null || g.getHomeScore() == null) continue;
                if (t1 == g.getAwayTeam() && t2 == g.getHomeTeam()) {
                    final Pair<Integer, Integer> points = FootballGroupStage.getPoints(g.getAwayScore(), g.getHomeScore());
                    t1Points += points.getKey();
                    t2Points += points.getValue();
                    t1Goals += g.getAwayScore();
                    t2Goals += g.getHomeScore();
                    t1Diff = t1Diff + g.getAwayScore() - g.getHomeScore();
                    t2Diff = t2Diff + g.getHomeScore() - g.getAwayScore();
                } else if (t1 == g.getHomeTeam() && t2 == g.getAwayTeam()) {
                    final Pair<Integer, Integer> points = FootballGroupStage.getPoints(g.getHomeScore(), g.getAwayScore());
                    t1Points += points.getKey();
                    t2Points += points.getValue();
                    t1Goals += g.getHomeScore();
                    t2Goals += g.getAwayScore();
                    t1Diff = t1Diff + g.getHomeScore() - g.getAwayScore();
                    t2Diff = t2Diff + g.getAwayScore() - g.getHomeScore();
                }
            }
        }
        return new HeadToHeadData(t1Points, t2Points, t1Goals, t2Goals, t1Diff, t2Diff);
    }
}

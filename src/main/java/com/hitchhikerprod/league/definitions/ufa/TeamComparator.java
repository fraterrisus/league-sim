package com.hitchhikerprod.league.definitions.ufa;

import java.util.Comparator;
import java.util.List;

public class TeamComparator implements Comparator<TeamData> {
    private final List<MatchDay> matchDays;

    public TeamComparator(List<MatchDay> matchDays) {
        this.matchDays = matchDays;
    }

    @Override
    public int compare(TeamData t1, TeamData t2) {
        // #1: overall win percentage
        final int c = Double.compare(t1.getWinPercentage(), t2.getWinPercentage());
        if (c != 0) return c;

        // #2: head-to-head wins
        int t1HeadWins = 0;
        int t2HeadWins = 0;
        for (MatchDay md : matchDays) {
            for (GameData g : md.games) {
                if (g.getAwayScore() == null || g.getHomeScore() == null) continue;
                if (t1 == g.getAwayTeam() && t2 == g.getHomeTeam()) {
                    if (g.getAwayScore() > g.getHomeScore()) t1HeadWins++;
                    else t2HeadWins++;
                } else if (t1 == g.getHomeTeam() && t2 == g.getAwayTeam()) {
                    if (g.getHomeScore() > g.getAwayScore()) t1HeadWins++;
                    else t2HeadWins++;
                }
            }
        }
        return Integer.compare(t1HeadWins, t2HeadWins);
    }
}

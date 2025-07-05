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
        int c = Double.compare(t1.getWinPercentage(), t2.getWinPercentage());
        if (c != 0) return c;

        int t1HeadWins = 0;
        int t1HeadGD = 0;
        int t2HeadWins = 0;
        int t2HeadGD = 0;
        for (MatchDay md : matchDays) {
            for (GameData g : md.games) {
                if (g.getAwayScore() == null || g.getHomeScore() == null) continue;
                if (t1 == g.getAwayTeam() && t2 == g.getHomeTeam()) {
                    t1HeadGD += g.getAwayScore() - g.getHomeScore();
                    t2HeadGD += g.getHomeScore() - g.getAwayScore();
                    if (g.getAwayScore() > g.getHomeScore()) t1HeadWins++;
                    else t2HeadWins++;
                } else if (t1 == g.getHomeTeam() && t2 == g.getAwayTeam()) {
                    t1HeadGD += g.getHomeScore() - g.getAwayScore();
                    t2HeadGD += g.getAwayScore() - g.getHomeScore();
                    if (g.getHomeScore() > g.getAwayScore()) t1HeadWins++;
                    else t2HeadWins++;
                }
            }
        }

        // #2: head-to-head wins
        c = Integer.compare(t1HeadWins, t2HeadWins);
        if (c != 0) return c;

        // #3: head-to-head goal difference
        return Integer.compare(t1HeadGD, t2HeadGD);
    }
}

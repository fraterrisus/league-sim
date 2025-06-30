package com.hitchhikerprod.league.definitions.football;

import java.util.Comparator;
import java.util.List;

public class TeamComparator implements Comparator<TeamData> {
    private final List<MatchDay> matchDays;

    public TeamComparator(List<MatchDay> matchDays) {
        this.matchDays = matchDays;
    }

    @Override
    public int compare(TeamData t1, TeamData t2) {
        // #1: points collected
        int c = Integer.compare(t1.getPoints(), t2.getPoints());
        if (c != 0) return c;
        
        // #2: goals scored
        c = Integer.compare(t1.getGoalsFor(), t2.getGoalsFor());
        if (c != 0) return c;

        // Head-to-head record won't quite work in cases where multiple teams are on the same number of points,
        // because we can't sort based on the entire list this way.
        int t1HeadWins = 0;
        int t2HeadWins = 0;
        int t1HeadGoals = 0;
        int t2HeadGoals = 0;
        for (MatchDay md : matchDays) {
            for (GameData g : md.games) {
                if (g.getAwayScore() == null || g.getHomeScore() == null) continue;
                if (t1 == g.getAwayTeam() && t2 == g.getHomeTeam()) {
                    if (g.getAwayScore() > g.getHomeScore()) t1HeadWins++;
                    else t2HeadWins++;
                    t1HeadGoals += g.getAwayScore();
                    t2HeadGoals += g.getHomeScore();
                } else if (t1 == g.getHomeTeam() && t2 == g.getAwayTeam()) {
                    if (g.getHomeScore() > g.getAwayScore()) t1HeadWins++;
                    else t2HeadWins++;
                    t1HeadGoals += g.getHomeScore();
                    t2HeadGoals += g.getAwayScore();
                }
            }
        }

        // #3: head-to-head points (wins, really)
        c = Integer.compare(t1HeadWins, t2HeadWins);
        if (c != 0) return c;

        // #4: head-to-head goals scored
        return Integer.compare(t1HeadGoals, t2HeadGoals);
        // #5: fair play points (untracked)
    }
}

package com.hitchhikerprod.league.definitions.football;

import java.util.Comparator;
import java.util.List;

public class GoldCupTeamComparator implements Comparator<TeamData> {
    private final List<MatchDay> matchDays;

    public GoldCupTeamComparator(List<MatchDay> matchDays) {
        this.matchDays = matchDays;
    }

    @Override
    public int compare(TeamData t1, TeamData t2) {
        //  - points collected
        //  - goals scored
        //  - head-to-head points
        //  - head-to-head goals
        //  - fair play points
        if (t1.equals(t2)) return 0;

        int c = Integer.compare(t1.getPoints(), t2.getPoints());
        if (c != 0) return c;
        c = Integer.compare(t1.getGoalsFor(), t2.getGoalsFor());
        if (c != 0) return c;

        final TeamData.HeadToHeadData hthData = TeamData.getHeadToHeadData(this.matchDays, t1, t2);
        c = Integer.compare(hthData.t1Points(), hthData.t2Points());
        if (c != 0) return c;
        return Integer.compare(hthData.t1Goals(), hthData.t2Goals());
    }
}

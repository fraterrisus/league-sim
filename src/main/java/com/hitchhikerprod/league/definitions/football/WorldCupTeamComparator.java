package com.hitchhikerprod.league.definitions.football;

import java.util.Comparator;

public class WorldCupTeamComparator implements Comparator<TeamData> {
    private final Iterable<MatchDay> matchDays;

    public WorldCupTeamComparator(Iterable<MatchDay> matchDays) {
        this.matchDays = matchDays;
    }

    @Override
    public int compare(TeamData t1, TeamData t2) {
        // 2026:
        //  - points collected
        //  - head-to-head points
        //  - head-to-head goal diff
        //  - head-to-head goals for
        //  - goal diff
        //  - goals for
        //  - fair play points
        //  - highest FIFA ranking
        if (t1.equals(t2)) return 0;

        int c = Integer.compare(t1.getPoints(), t2.getPoints());
        if (c != 0) return c;

        final TeamData.HeadToHeadData hthData = TeamData.getHeadToHeadData(this.matchDays, t1, t2);
        c = Integer.compare(hthData.t1Points(), hthData.t2Points());
        if (c != 0) return c;
        c = Integer.compare(hthData.t1Diff(), hthData.t2Diff());
        if (c != 0) return c;
        c = Integer.compare(hthData.t1Goals(), hthData.t2Goals());
        if (c != 0) return c;

        c = Integer.compare(t1.goalsFor - t1.goalsAgainst, t2.goalsFor - t2.goalsAgainst);
        if (c != 0) return c;
        return Integer.compare(t1.goalsFor, t2.goalsFor);
    }
}

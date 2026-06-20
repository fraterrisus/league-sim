package com.hitchhikerprod.league.definitions.football;

import java.util.Comparator;
import java.util.List;

public class WorldCupTeamComparator implements Comparator<TeamData> {
    private final List<MatchDay> matchDays;

    public WorldCupTeamComparator(List<MatchDay> matchDays) {
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

        System.out.println("Tie breaker: " + t1.fullName + " v " + t2.fullName);

        final TeamData.HeadToHeadData hthData = TeamData.getHeadToHeadData(this.matchDays, t1, t2);
        System.out.printf("hPt: %d - %d\n", hthData.t1Points(), hthData.t2Points());
        c = Integer.compare(hthData.t1Points(), hthData.t2Points());
        if (c != 0) return c;
        System.out.printf("hGD: %d - %d\n", hthData.t1Diff(), hthData.t2Diff());
        c = Integer.compare(hthData.t1Diff(), hthData.t2Diff());
        if (c != 0) return c;
        System.out.printf("hGF: %d - %d\n", hthData.t1Goals(), hthData.t2Goals());
        c = Integer.compare(hthData.t1Goals(), hthData.t2Goals());
        if (c != 0) return c;

        System.out.printf("GD : %d - %d\n", t1.goalsFor - t1.goalsAgainst, t2.goalsFor - t2.goalsAgainst);
        c = Integer.compare(t1.goalsFor - t1.goalsAgainst, t2.goalsFor - t2.goalsAgainst);
        if (c != 0) return c;
        System.out.printf("GF : %d - %d\n", t1.goalsFor, t2.goalsFor);
        return Integer.compare(t1.goalsFor, t2.goalsFor);
    }
}

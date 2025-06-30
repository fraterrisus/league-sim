package com.hitchhikerprod.league.definitions.afl;

import java.util.Comparator;

class TeamComparator implements Comparator<TeamData> {
    @Override
    public int compare(TeamData o1, TeamData o2) {
        final int c = Integer.compare(o1.getPoints(), o2.getPoints());
        if (c != 0) return c;

        return Double.compare(o1.getPercentage(), o2.getPercentage());
    }
}

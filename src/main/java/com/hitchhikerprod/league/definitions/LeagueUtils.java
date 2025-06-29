package com.hitchhikerprod.league.definitions;

import com.hitchhikerprod.league.beans.LeagueMatchDay;

import java.util.List;

public class LeagueUtils {
    public static int getLatestCompleteMatchDay(List<? extends LeagueMatchDay> matchDays) {
        if (matchDays.size() == 1) return 0;
        for (int idx = 0; idx < matchDays.size(); idx++) {
            LeagueMatchDay matchDay = matchDays.get(idx);
            if (!matchDay.isComplete()) return idx - 1;
        }
        return matchDays.size() - 1;
    }
}

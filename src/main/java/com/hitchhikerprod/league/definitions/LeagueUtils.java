package com.hitchhikerprod.league.definitions;

import com.hitchhikerprod.league.beans.LeagueColumn;
import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;
import com.hitchhikerprod.league.util.ColumnDef;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeagueUtils {
    public static <T> List<LeagueColumn<?>> getDivisionColumns(List<ColumnDef<T,?>> columns) {
        return IntStream.range(0, columns.size())
                .mapToObj(idx -> columns.get(idx).toColumn(idx))
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<? extends LeagueGameData> getGames(int matchDayIndex, List<? extends LeagueMatchDay> matchDays) {
        if (matchDayIndex < 0) { return List.of(); }
        return matchDays.get(matchDayIndex).getGames();
    }

    public static int getLatestCompleteMatchDay(List<? extends LeagueMatchDay> matchDays) {
        if (matchDays.size() == 1) return 0;
        for (int idx = 0; idx < matchDays.size(); idx++) {
            LeagueMatchDay matchDay = matchDays.get(idx);
            if (!matchDay.isComplete()) return idx - 1;
        }
        return matchDays.size() - 1;
    }

    public static List<String> getMatchDays(List<? extends LeagueMatchDay> matchDays) {
        return matchDays.stream().map(LeagueMatchDay::getName).toList();
    }
}

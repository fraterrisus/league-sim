package com.hitchhikerprod.league.definitions;

import com.hitchhikerprod.league.beans.LeagueColumn;
import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.beans.RawGame;
import com.hitchhikerprod.league.beans.RawLeagueData;
import com.hitchhikerprod.league.beans.RawTeamData;
import com.hitchhikerprod.league.util.ColumnDef;
import com.hitchhikerprod.league.util.Converter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** A library of abstracted, templatized functions that are used by every League implementation. */
public class LeagueUtils {
    public static <T> List<LeagueColumn<?>> getDivisionColumns(List<ColumnDef<T,?>> columns) {
        return IntStream.range(0, columns.size())
                .mapToObj(idx -> columns.get(idx).toColumn(idx))
                .collect(Collectors.toUnmodifiableList());
    }

    public static ObservableList<? extends LeagueGameData> getGames(int matchDayIndex, List<? extends LeagueMatchDay> matchDays) {
        // Hm.
        if (matchDayIndex < 0) { return FXCollections.emptyObservableList(); }
        return matchDays.get(matchDayIndex).getGames();
    }

    public static int getLatestCompleteMatchDay(List<? extends LeagueMatchDay> matchDays) {
        for (int idx = 0; idx < matchDays.size(); idx++) {
            LeagueMatchDay matchDay = matchDays.get(idx);
            if (!matchDay.isComplete()) return Integer.max(0, idx - 1);
        }
        return matchDays.size() - 1;
    }

    @FunctionalInterface
    public interface LeagueFactory<L, T, M> {
        L from(Map<String, T> teamData, RawLeagueData leagueData, List<M> matchDayData);
    }

    public static <
            L extends League,
            T extends LeagueTeamData,
            G extends LeagueGameData,
            M extends LeagueMatchDay
    > L newLeagueFrom(
            RawLeagueData leagueData,
            Converter<RawTeamData, T> teamDataConverter,
            Function<Map<String, T>, Converter<RawGame, G>> gameDataConverterFactory,
            Function<String, M> matchDayFactory,
            LeagueFactory<L, T, M> leagueFactory
    ) {
        final Map<String, T> teams = leagueData.teams.stream()
                .map(teamDataConverter::convert)
                .collect(Collectors.toMap(T::getId, t -> t));

        final Converter<RawGame, G> rawConverter = gameDataConverterFactory.apply(teams);

        final List<M> matchDays = leagueData.matchdays.stream().map(md -> {
            final M matchDay = matchDayFactory.apply(md.getName());
            final List<G> gameData = md.getGames().stream().map(rawConverter::convert).toList();
            matchDay.setGames(gameData);
            matchDay.setComplete(gameData.stream().allMatch(G::isComplete));
            return matchDay;
        }).collect(Collectors.toList());

        return leagueFactory.from(teams, leagueData, matchDays);
    }
}

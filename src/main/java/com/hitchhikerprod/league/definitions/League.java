package com.hitchhikerprod.league.definitions;

import com.hitchhikerprod.league.beans.LeagueColumn;
import com.hitchhikerprod.league.beans.LeagueDivision;
import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.beans.RawLeagueData;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;

public interface League {
    int getLatestCompleteMatchDay();
    RawLeagueData export();
    ObservableList<? extends LeagueMatchDay> getMatchDays();
    void addMatchDay(int index, String name);
    void addMatchDay(String name);
    ObservableList<? extends LeagueDivision> getDivisions();
    Map<? extends LeagueDivision, List<? extends LeagueTeamData>> getDivisionTables(int index);
    List<LeagueColumn<?>> getDivisionColumns();
    ObservableList<? extends LeagueGameData> getGames(int matchDayIndex);
    List<LeagueMatchDay> getGames(LeagueTeamData teamData);
    void createGame(int matchDayIndex, String awayTeamId, String homeTeamId);
    List<? extends LeagueTeamData> getTeams();
}

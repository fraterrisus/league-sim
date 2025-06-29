package com.hitchhikerprod.league;

import com.hitchhikerprod.league.beans.LeagueColumn;
import com.hitchhikerprod.league.beans.LeagueDivision;
import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.beans.RawLeagueData;

import java.util.List;
import java.util.Map;

public interface League {
    int getLatestCompleteMatchDay();
    List<String> getMatchDays();
    Map<? extends LeagueDivision, List<? extends LeagueTeamData>> getDivisionTables(int index);
    List<? extends LeagueGameData> getGames(int matchDay);
    List<LeagueMatchDay> getGames(LeagueTeamData teamData);
    RawLeagueData export();
    List<LeagueColumn<?>> getDivisionColumns();
}

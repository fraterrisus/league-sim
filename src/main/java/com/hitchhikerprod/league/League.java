package com.hitchhikerprod.league;

import com.hitchhikerprod.league.beans.RawDivision;
import com.hitchhikerprod.league.beans.RawLeagueData;
import com.hitchhikerprod.league.definitions.UFA2025;

import java.util.List;
import java.util.Map;

public interface League {
    int getLatestCompleteMatchDay();
    List<String> getMatchDays();
    Map<RawDivision, List<UFA2025.TeamData>> getDivisionTables(int index);
    List<UFA2025.UFAGameData> getGames(int matchDay);
    RawLeagueData export();
}

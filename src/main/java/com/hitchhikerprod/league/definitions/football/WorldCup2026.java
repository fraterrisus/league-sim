package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.RawLeagueData;
import com.hitchhikerprod.league.definitions.LeagueUtils;

import java.util.List;
import java.util.Map;

public class WorldCup2026 extends FootballGroupStage {
    public static final String LEAGUE_TYPE = "world-cup-2026";

    public WorldCup2026(Map<String, TeamData> teams, RawLeagueData leagueData, List<MatchDay> matchDays) {
        super(teams, leagueData, matchDays, WorldCupTeamComparator::new);
    }

    public static WorldCup2026 from(RawLeagueData leagueData) {
        return LeagueUtils.newLeagueFrom(
                leagueData,
                t -> new TeamData(t.getName(), t.getId()),
                GameRawConverter::new,
                MatchDay::new,
                WorldCup2026::new
        );
    }
}

package com.hitchhikerprod.league.definitions.football;

import com.hitchhikerprod.league.beans.RawLeagueData;
import com.hitchhikerprod.league.definitions.LeagueUtils;

import java.util.List;
import java.util.Map;

public class GoldCup2025 extends FootballGroupStage {
    public static final String LEAGUE_TYPE = "gold-cup-2025";

    public GoldCup2025(Map<String, TeamData> teams, RawLeagueData leagueData, List<MatchDay> matchDays) {
        super(teams, leagueData, matchDays, GoldCupTeamComparator::new);
    }

    public static GoldCup2025 from(RawLeagueData leagueData) {
        return LeagueUtils.newLeagueFrom(
                leagueData,
                t -> new TeamData(t.getName(), t.getId()),
                GameRawConverter::new,
                MatchDay::new,
                GoldCup2025::new
        );
    }
}

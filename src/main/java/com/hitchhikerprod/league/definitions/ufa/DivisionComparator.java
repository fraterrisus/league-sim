package com.hitchhikerprod.league.definitions.ufa;

import javafx.collections.ObservableList;
import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DivisionComparator {
    private final ObservableList<MatchDay> matchDays;
    private final List<TeamData> teams;

    public DivisionComparator(ObservableList<MatchDay> matchDays, List<TeamData> teamsIn) {
        this.matchDays = matchDays;
        this.teams = teamsIn;
    }

    private record TeamRecord(int wins, int losses, int goalsFor, int goalsAgainst) {
        public TeamRecord addGame(int goalsFor, int goalsAgainst) {
            if (goalsFor > goalsAgainst) {
                return new TeamRecord(this.wins + 1, this.losses, this.goalsFor + goalsFor, this.goalsAgainst + goalsAgainst);
            } else {
                return new TeamRecord(this.wins, this.losses + 1, this.goalsFor + goalsFor, this.goalsAgainst + goalsAgainst);
            }
        }
    }

    private record CompareRecord(
            TeamData teamData,
            TeamRecord teamRecord,
            List<TeamRecord> headToHead
    ) {}

    public List<TeamData> rank() {
        final List<TeamData> finalRanking = new ArrayList<>();
        final List<CompareRecord> compareRecords = collectRecords();
//        System.out.println("**");
        rankByWins(finalRanking, compareRecords);
        return finalRanking;
    }

    private void rankByWins(List<TeamData> finalRanking, Iterable<CompareRecord> compareRecords) {
        final Map<Fraction, Set<CompareRecord>> rankingMap = new HashMap<>();
        for (CompareRecord cr : compareRecords) {
            final Fraction winFrac = Fraction.getFraction(cr.teamRecord.wins, cr.teamRecord.wins + cr.teamRecord.losses);

            if (!rankingMap.containsKey(winFrac)) rankingMap.put(winFrac, new HashSet<>());
            final Set<CompareRecord> records = rankingMap.get(winFrac);
            records.add(cr);
        }

        final List<Fraction> winFracList = rankingMap.keySet().stream().sorted().toList();
        for (Fraction wf : winFracList) {
            final Set<CompareRecord> teamRecords = rankingMap.get(wf);
            if (teamRecords.size() == 1) {
                teamRecords.forEach(tr -> {
//                    System.out.printf("%s (%d-%d)\n", tr.teamData.getName(), tr.teamRecord.wins, tr.teamRecord.losses);
                    finalRanking.add(tr.teamData);
                });
                continue;
            }
            rankByHeadToHeadRecord(finalRanking, teamRecords);
        }
    }

    private void rankByHeadToHeadRecord(List<TeamData> finalRanking, Iterable<CompareRecord> compareRecords) {
        final Map<Integer, Set<CompareRecord>> rankingMap = new HashMap<>();
        for (CompareRecord myRecord : compareRecords) {
            int wins = 0;
            for (CompareRecord theirRecord : compareRecords) {
                if (myRecord == theirRecord) continue;
                final int theirIndex = teams.indexOf(theirRecord.teamData);
                final TeamRecord headRec = myRecord.headToHead.get(theirIndex);

                wins += Integer.compare(headRec.wins(), headRec.losses());
            }

            if (!rankingMap.containsKey(wins)) rankingMap.put(wins, new HashSet<>());
            final Set<CompareRecord> records = rankingMap.get(wins);
            records.add(myRecord);
        }

        final List<Integer> winList = rankingMap.keySet().stream().sorted().toList();
        for (Integer w : winList) {
            final Set<CompareRecord> teamRecords = rankingMap.get(w);
            if (teamRecords.size() == 1) {
                teamRecords.forEach(tr -> {
//                    System.out.printf("%s (%d-%d) (%d HTH)\n", tr.teamData.getName(), tr.teamRecord.wins,
//                            tr.teamRecord.losses, w);
                    finalRanking.add(tr.teamData);
                });
                continue;
            }
            rankByHeadToHeadGoals(finalRanking, teamRecords);
        }
    }

    private void rankByHeadToHeadGoals(List<TeamData> finalRanking, Iterable<CompareRecord> compareRecords) {
        final Map<Integer, Set<CompareRecord>> rankingMap = new HashMap<>();
        for (CompareRecord myRecord : compareRecords) {
            int wins = 0;
            for (CompareRecord theirRecord : compareRecords) {
                if (myRecord == theirRecord) continue;
                final int theirIndex = teams.indexOf(theirRecord.teamData);
                final TeamRecord headRec = myRecord.headToHead.get(theirIndex);

                wins += Integer.compare(headRec.goalsFor(), headRec.goalsAgainst());
            }

            if (!rankingMap.containsKey(wins)) rankingMap.put(wins, new HashSet<>());
            final Set<CompareRecord> records = rankingMap.get(wins);
            records.add(myRecord);
        }

        final List<Integer> goalList = rankingMap.keySet().stream().sorted().toList();
        for (Integer gd : goalList) {
            final Set<CompareRecord> teamRecords = rankingMap.get(gd);
//            if (teamRecords.size() != 1) {
//                System.out.println("Giving up");
//            }
            teamRecords.forEach(tr -> {
//                System.out.printf("%s (%d-%d) (%d GF)\n", tr.teamData.getName(), tr.teamRecord.wins,
//                        tr.teamRecord.losses, gd);
                finalRanking.add(tr.teamData);
            });
        }
    }

    private List<CompareRecord> collectRecords() {
        final List<CompareRecord> compareRecords = new ArrayList<>();
        for (TeamData team : teams) {
            TeamRecord record = new TeamRecord(0, 0, 0, 0);
            final List<TeamRecord> headToHead = new ArrayList<>();
            for (int i = 0; i < teams.size(); i++) { headToHead.add(new TeamRecord(0, 0, 0, 0)); }
            for (MatchDay md : matchDays) {
                for (GameData game : md.games) {
                    if (game.getAwayScore() == null || game.getHomeScore() == null) continue;
                    if (game.getHomeTeam() == team) {
                        record = record.addGame(game.getHomeScore(), game.getAwayScore());
                        final int index = teams.indexOf(game.getAwayTeam());
                        if (index != -1)
                            headToHead.set(index, headToHead.get(index).addGame(game.getHomeScore(), game.getAwayScore()));
                    } else if (game.getAwayTeam() == team) {
                        record = record.addGame(game.getAwayScore(), game.getHomeScore());
                        final int index = teams.indexOf(game.getHomeTeam());
                        if (index != -1)
                            headToHead.set(index, headToHead.get(index).addGame(game.getAwayScore(), game.getHomeScore()));
                    }
                }
            }
            compareRecords.add(new CompareRecord(team, record, headToHead));
        }
        return compareRecords;
    }
}

package com.hitchhikerprod.league.beans;

import java.util.Map;

public class Game {
    public String homeTeam;
    public Object homeScore;
    public String awayTeam;
    public Object awayScore;

    private Double interpretScore(Object score, Map<String, Double> scoreMap) {
        if (score == null) return null;
        if (scoreMap == null) {
            return interpretScalarScore(score);
        } else {
            if (! (score instanceof Map)) {
                throw new RuntimeException("Unrecognized score type " + score.getClass());
            }
            return interpretMapScore((Map)score, scoreMap);
        }
    }

    private static double interpretMapScore(Map score, Map<String, Double> scoreMap) {
        double value = 0.0;
        for (Map.Entry<String, Double> entry : scoreMap.entrySet()) {
            final Double gameValue;
            final Object rawGameValue = score.get(entry.getKey());
            if (rawGameValue instanceof Double) {
                gameValue = (Double)rawGameValue;
            } else if (rawGameValue instanceof Integer) {
                gameValue = Double.valueOf((Integer)rawGameValue);
            } else {
                throw new RuntimeException("Unrecognized score value at key " + entry.getKey() + " of type " + rawGameValue.getClass());
            }
            value += gameValue * entry.getValue();
        }
        return value;
    }

    private static double interpretScalarScore(Object score) {
        if (score instanceof Double) {
            return (Double) score;
        } else if (score instanceof Integer) {
            return Double.valueOf((Integer) score);
        } else {
            throw new RuntimeException("Unrecognized score type " + score.getClass());
        }
    }

    public Double getHomeValue(Map<String, Double> scoreMap) {
        return interpretScore(this.homeScore, scoreMap);
    }

    public Double getAwayValue(Map<String, Double> scoreMap) {
        return interpretScore(this.awayScore, scoreMap);
    }
}

package com.hitchhikerprod.league.beans;

public interface LeagueTeamData {
    String getName();
    String getId();
    <T> T getData(Class<T> klass, int index);
}

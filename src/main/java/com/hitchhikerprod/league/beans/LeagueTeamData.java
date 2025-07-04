package com.hitchhikerprod.league.beans;

public interface LeagueTeamData extends Named {
    String getId();
    <T> T getData(Class<T> klass, int index);
}

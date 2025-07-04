package com.hitchhikerprod.league.beans;

public class RawLeague {
    public String name;
    public String type;

    public static RawLeague from(String name, String type) {
        final RawLeague league = new RawLeague();
        league.name = name;
        league.type = type;
        return league;
    }
}

package com.hitchhikerprod.league.beans;

public class RawTeamData implements LeagueTeamData {
    private String name;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public <T> T getData(Class<T> klass, int index) {
        return null;
    }
}

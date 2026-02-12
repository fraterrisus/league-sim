package com.hitchhikerprod.league.beans;

import java.util.List;
import java.util.Objects;

public class RawMatchDay {
    private String name;
    private List<RawGame> games;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RawGame> getGames() {
        if (Objects.isNull(games)) return List.of();
        return games;
    }

    public void setGames(List<RawGame> games) {
        this.games = games;
    }
}

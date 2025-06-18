package com.hitchhikerprod.league.beans;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    @Test
    void interpretScalarIntScore() {
        final Game g = new Game();
        g.homeScore = Integer.valueOf(5);
        final Double value = g.getHomeValue(null);
        assertEquals(5, value);
    }

    @Test
    void interpretScalarDoubleScore() {
        final Game g = new Game();
        g.homeScore = Double.valueOf(1.75);
        final Double value = g.getHomeValue(null);
        assertEquals(1.75, value);
    }

    @Test
    void interpretMapIntScore() {
        final Game g = new Game();
        g.homeScore = Map.of("goals", 5, "behinds", 4);
        final Double value = g.getHomeValue(Map.of("goals", 6.0, "behinds", 1.0));
        assertEquals(34.0, value);
    }

    @Test
    void interpretMapDoubleScore() {
        final Game g = new Game();
        g.homeScore = Map.of("goals", 1.75);
        final Double value = g.getHomeValue(Map.of("goals", 2.0));
        assertEquals(3.5, value);
    }
}
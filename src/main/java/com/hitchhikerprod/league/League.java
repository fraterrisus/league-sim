package com.hitchhikerprod.league;

import com.hitchhikerprod.league.beans.Division;
import com.hitchhikerprod.league.definitions.UFA2025;

import java.util.List;
import java.util.Map;

public interface League {
    Map<Division, List<UFA2025.TeamData>> getDivisionTables();
    String tables();
}

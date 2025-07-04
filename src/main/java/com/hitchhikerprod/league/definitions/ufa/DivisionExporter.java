package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.RawDivision;
import com.hitchhikerprod.league.util.Converter;

public class DivisionExporter implements Converter<Division, RawDivision> {
    @Override
    public RawDivision convert(Division that) {
        final RawDivision rawDivision = new RawDivision();
        rawDivision.setName(that.getName());
        rawDivision.setTeams(that.getObservableTeams().stream().map(TeamData::getName).toList());
        return rawDivision;
    }
}

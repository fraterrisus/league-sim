package com.hitchhikerprod.league.definitions.ufa;

import com.hitchhikerprod.league.beans.RawTeamData;
import com.hitchhikerprod.league.util.Converter;

import java.util.stream.Stream;

public class TeamExporter implements Converter<Division, Stream<RawTeamData>> {
    @Override
    public Stream<RawTeamData> convert(Division that) {
        return that.getObservableTeams().stream()
                .map(teamData -> RawTeamData.from(teamData.getName(), teamData.getId()));
    }
}

package com.hitchhikerprod.league.util;

import com.hitchhikerprod.league.beans.LeagueColumn;
import javafx.geometry.Pos;

import java.util.Optional;
import java.util.function.Function;

public record ColumnDef<S, T>(
        Class<T> klass,
        String header,
        Pos align,
        Optional<String> fmt,
        Function<S, T> getter
) {
    public LeagueColumn<T> toColumn(int index) {
        return fmt.map(format -> new LeagueColumn<>(index, klass, header, align, format))
                .orElseGet(() -> new LeagueColumn<>(index, klass, header, align));
    }
}

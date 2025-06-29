package com.hitchhikerprod.league.util;

public interface Converter<S,T> {
    T convert(S that);
}

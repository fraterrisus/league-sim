package com.hitchhikerprod.league.util;

import java.util.List;

public class AtomicStringList {
    private List<String> data;

    public synchronized List<String> get() {
        return data;
    }

    public synchronized void set(List<String> data) {
        this.data = data;
    }
}

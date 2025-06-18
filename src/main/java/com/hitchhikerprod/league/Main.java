package com.hitchhikerprod.league;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final List<String> document = new ArrayList<>();
        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(args[0]), StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                document.add(line);
            }
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }

        final League l = LeagueFactory.fromYaml(String.join("\n", document));
        System.out.print(l.tables());
    }
}
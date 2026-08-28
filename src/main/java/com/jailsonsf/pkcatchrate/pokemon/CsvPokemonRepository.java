package com.jailsonsf.pkcatchrate.pokemon;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Repository
public class CsvPokemonRepository implements PokemonRepository {

    private static final int NAME_INDEX = 0;
    private static final int PRIMARY_TYPE_INDEX = 2;
    private static final int SECONDARY_TYPE_INDEX = 3;
    private static final int CATCH_RATE_INDEX = 12;
    private static final int BASE_HP_INDEX = 18;

    private final Map<String, PokemonSpecies> byName = new HashMap<>();

    public CsvPokemonRepository() {
        load();
    }

    private void load() {
        ClassPathResource resource = new ClassPathResource("pokemon/all_pokemon_data.csv");
        try (InputStream input = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String header = reader.readLine();
            if (header == null) {
                throw new IllegalStateException("Empty dataset CSV");
            }
            reader.lines().forEach(this::parseRow);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load Pokemon dataset", e);
        }
    }

    private void parseRow(String line) {
        String[] columns = line.split(",", -1);
        if (columns.length <= BASE_HP_INDEX) {
            return;
        }
        String name = normalize(columns[NAME_INDEX]);
        if (name.isEmpty()) {
            return;
        }
        String secondaryType = columns[SECONDARY_TYPE_INDEX].isBlank() ? null : columns[SECONDARY_TYPE_INDEX];
        byName.put(name, new PokemonSpecies(
                name,
                Integer.parseInt(columns[CATCH_RATE_INDEX]),
                Integer.parseInt(columns[BASE_HP_INDEX]),
                columns[PRIMARY_TYPE_INDEX],
                Optional.ofNullable(secondaryType)));
    }

    @Override
    public Optional<PokemonSpecies> findByName(String name) {
        return Optional.ofNullable(byName.get(normalize(name)));
    }

    private static String normalize(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }
}

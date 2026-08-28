package com.jailsonsf.pkcatchrate.pokemon;

import java.util.Optional;

public record PokemonSpecies(
        String name,
        int catchRate,
        int baseHp,
        String primaryType,
        Optional<String> secondaryType) {
}

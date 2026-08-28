package com.jailsonsf.pkcatchrate.pokemon;

import java.util.Optional;

public interface PokemonRepository {

    Optional<PokemonSpecies> findByName(String name);
}

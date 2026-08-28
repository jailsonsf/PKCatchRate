package com.jailsonsf.pkcatchrate.pokemon;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CsvPokemonRepositoryTest {

    private final PokemonRepository repository = new CsvPokemonRepository();

    @Test
    void findsPikachuWithExpectedConstants() {
        Optional<PokemonSpecies> found = repository.findByName("pikachu");

        assertThat(found).hasValueSatisfying(species -> {
            assertThat(species.catchRate()).isEqualTo(190);
            assertThat(species.baseHp()).isEqualTo(35);
            assertThat(species.primaryType()).isEqualTo("electric");
        });
    }

    @Test
    void findsMewtwoWithExpectedConstants() {
        Optional<PokemonSpecies> found = repository.findByName("mewtwo");

        assertThat(found).hasValueSatisfying(species -> {
            assertThat(species.catchRate()).isEqualTo(3);
            assertThat(species.baseHp()).isEqualTo(106);
            assertThat(species.primaryType()).isEqualTo("psychic");
        });
    }

    @Test
    void returnsEmptyForUnknownSpecies() {
        assertThat(repository.findByName("missingno")).isEmpty();
    }
}

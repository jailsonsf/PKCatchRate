package com.jailsonsf.pkcatchrate.web;

import com.jailsonsf.pkcatchrate.engine.CatchEngine;
import com.jailsonsf.pkcatchrate.pokemon.PokemonRepository;
import com.jailsonsf.pkcatchrate.pokemon.PokemonSpecies;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.closeTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatchRateController.class)
@Import({CatchRateExceptionHandler.class, CatchEngine.class})
class CatchRateControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PokemonRepository repository;

    @Test
    void happyPathComputesThroughTheRealEngine() throws Exception {
        when(repository.findByName("pikachu")).thenReturn(Optional.of(
                new PokemonSpecies("pikachu", 190, 35, "electric", Optional.empty())));

        mockMvc.perform(post("/api/catch-rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "pikachu",
                      "level": 50,
                      "currentHp": 110,
                      "ball": "POKE"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.a").value(63))
                .andExpect(jsonPath("$.b").value(47661))
                .andExpect(jsonPath("$.probability").value(closeTo(27.97, 0.01)))
                .andExpect(jsonPath("$.guaranteed").value(false));
    }

    @Test
    void clampsCurrentHpAboveMaxHp() throws Exception {
        when(repository.findByName("pikachu")).thenReturn(Optional.of(
                new PokemonSpecies("pikachu", 190, 35, "electric", Optional.empty())));

        mockMvc.perform(post("/api/catch-rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "pikachu",
                      "level": 50,
                      "currentHp": 9999,
                      "ball": "POKE"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.a").value(63))
                .andExpect(jsonPath("$.b").value(47661))
                .andExpect(jsonPath("$.probability").value(closeTo(27.97, 0.01)));
    }

    @Test
    void unknownSpeciesReturnsNotFoundWithConsistentBody() throws Exception {
        when(repository.findByName(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/catch-rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "missingno",
                      "level": 50,
                      "currentHp": 110,
                      "ball": "POKE"
                    }
                    """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("UNKNOWN_POKEMON"))
                .andExpect(jsonPath("$.message").value("Unknown species: missingno"));
    }

    @Test
    void ballNotAvailableReturnsUnprocessableEntityWithConsistentBody() throws Exception {
        when(repository.findByName("mewtwo")).thenReturn(Optional.of(
                new PokemonSpecies("mewtwo", 3, 106, "psychic", Optional.empty())));

        mockMvc.perform(post("/api/catch-rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "mewtwo",
                      "level": 70,
                      "currentHp": 250,
                      "ball": "DREAM",
                      "status": "SLEEP",
                      "generation": "GEN_V"
                    }
                    """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("BALL_NOT_AVAILABLE"));
    }

    @Test
    void beanValidationRejectsBadDtoBeforeTheEngine() throws Exception {
        when(repository.findByName("pikachu")).thenReturn(Optional.of(
                new PokemonSpecies("pikachu", 190, 35, "electric", Optional.empty())));

        mockMvc.perform(post("/api/catch-rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "pikachu",
                      "level": 0,
                      "currentHp": 110,
                      "ball": "POKE"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
    }
}

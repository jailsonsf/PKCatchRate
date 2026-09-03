package com.jailsonsf.pkcatchrate.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CatchRateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void returnsCatchResultForPikachuWithPokeball() throws Exception {
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
  void masterBallReturnsGuaranteedCapture() throws Exception {
    mockMvc.perform(post("/api/catch-rate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "mewtwo",
              "level": 70,
              "currentHp": 250,
              "ball": "MASTER"
            }
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.guaranteed").value(true))
        .andExpect(jsonPath("$.probability").value(100.0));
  }

  @Test
  void rejectsLevelZero() throws Exception {
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
        .andExpect(status().isBadRequest());
  }

  @Test
  void rejectsLevelAboveOneHundred() throws Exception {
    mockMvc.perform(post("/api/catch-rate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "pikachu",
              "level": 101,
              "currentHp": 110,
              "ball": "POKE"
            }
            """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void rejectsZeroCurrentHp() throws Exception {
    mockMvc.perform(post("/api/catch-rate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "pikachu",
              "level": 50,
              "currentHp": 0,
              "ball": "POKE"
            }
            """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void returnsNotFoundForUnknownSpecies() throws Exception {
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
        .andExpect(status().isNotFound());
  }

  @Test
  void acceptsStatusAndGeneration() throws Exception {
    mockMvc.perform(post("/api/catch-rate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "pikachu",
              "level": 50,
              "currentHp": 110,
              "ball": "POKE",
              "status": "SLEEP",
              "generation": "GEN_V"
            }
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.a").value(158))
        .andExpect(jsonPath("$.b").value(58175))
        .andExpect(jsonPath("$.probability").value(closeTo(69.947, 0.01)));
  }

  @Test
  void defaultsToNoStatusButUsesRequestedGeneration() throws Exception {
    mockMvc.perform(post("/api/catch-rate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "pikachu",
              "level": 50,
              "currentHp": 110,
              "ball": "POKE",
              "generation": "GEN_VI_PLUS"
            }
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.a").value(63))
        .andExpect(jsonPath("$.b").value(50473))
        .andExpect(jsonPath("$.probability").value(closeTo(35.18, 0.01)));
  }

  @Test
  void rejectsStatusWithMoreThanOneValue() throws Exception {
    mockMvc.perform(post("/api/catch-rate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "pikachu",
              "level": 50,
              "currentHp": 110,
              "ball": "POKE",
              "status": ["SLEEP", "BURN"]
            }
            """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void quickBallOnFirstTurnInGenVBoostsThroughTheApi() throws Exception {
    mockMvc.perform(post("/api/catch-rate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "mewtwo",
              "level": 70,
              "currentHp": 250,
              "ball": "QUICK",
              "generation": "GEN_V",
              "battleContext": {
                "turnsElapsed": 0,
                "isFirstTurn": true,
                "isAlreadyCaught": false,
                "fromFishing": false,
                "onWater": false,
                "inCaveOrNight": false
              }
            }
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.a").value(5))
        .andExpect(jsonPath("$.b").value(24523))
        .andExpect(jsonPath("$.probability").value(closeTo(5.24, 0.01)));
  }

  @Test
  void battleContextIsOptionalAndDefaultsToNeutral() throws Exception {
    mockMvc.perform(post("/api/catch-rate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "mewtwo",
              "level": 70,
              "currentHp": 250,
              "ball": "QUICK",
              "generation": "GEN_V"
            }
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.a").value(1))
        .andExpect(jsonPath("$.b").value(16400));
  }

  @Test
  void duskBallInCaveOrNightReturnsBoostedCatchInGenV() throws Exception {
    mockMvc.perform(post("/api/catch-rate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "mewtwo",
              "level": 70,
              "currentHp": 250,
              "ball": "DUSK",
              "generation": "GEN_V",
              "battleContext": {
                "turnsElapsed": 0,
                "isFirstTurn": false,
                "isAlreadyCaught": false,
                "fromFishing": false,
                "onWater": false,
                "inCaveOrNight": true
              }
            }
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.a").value(3))
        .andExpect(jsonPath("$.b").value(22431))
        .andExpect(jsonPath("$.probability").value(closeTo(4.01, 0.01)));
  }

  @Test
  void dreamBallOutsideGen6PlusReturnsUnprocessableEntity() throws Exception {
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
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void excludedBallReturnsUnprocessableEntity() throws Exception {
    mockMvc.perform(post("/api/catch-rate")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "mewtwo",
              "level": 70,
              "currentHp": 250,
              "ball": "LURE",
              "generation": "GEN_VI_PLUS"
            }
            """))
        .andExpect(status().isUnprocessableEntity());
  }
}

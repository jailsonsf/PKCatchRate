package com.jailsonsf.pkcatchrate.web;

import com.jailsonsf.pkcatchrate.ball.PokeBall;
import com.jailsonsf.pkcatchrate.exception.BallNotAvailableException;
import com.jailsonsf.pkcatchrate.exception.InvalidHpException;
import com.jailsonsf.pkcatchrate.exception.InvalidLevelException;
import com.jailsonsf.pkcatchrate.exception.UnknownPokemonException;
import com.jailsonsf.pkcatchrate.generation.GameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class CatchRateExceptionHandlerTest {

    private final CatchRateExceptionHandler handler = new CatchRateExceptionHandler();

    @Test
    void unknownPokemonMapsToNotFound() {
        ResponseEntity<ApiError> response =
                handler.handleUnknownPokemon(new UnknownPokemonException("missingno"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(new ApiError(404, "UNKNOWN_POKEMON", "Unknown species: missingno"));
    }

    @Test
    void invalidLevelMapsToBadRequest() {
        ResponseEntity<ApiError> response =
                handler.handleInvalidLevel(new InvalidLevelException(150));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ApiError(400, "INVALID_LEVEL", "Level must be between 1 and 100 but was 150"));
    }

    @Test
    void invalidHpMapsToBadRequest() {
        ResponseEntity<ApiError> response =
                handler.handleInvalidHp(new InvalidHpException(0));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ApiError(400, "INVALID_HP", "Current HP must be at least 1 but was 0"));
    }

    @Test
    void ballNotAvailableMapsToUnprocessableEntity() {
        ResponseEntity<ApiError> response =
                handler.handleBallNotAvailable(new BallNotAvailableException(PokeBall.DREAM, GameGeneration.GEN_V));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isEqualTo(new ApiError(422, "BALL_NOT_AVAILABLE", "Ball DREAM is not available in GEN_V"));
    }
}

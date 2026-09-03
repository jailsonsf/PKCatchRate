package com.jailsonsf.pkcatchrate.web;

import com.jailsonsf.pkcatchrate.engine.BallNotAvailableException;
import com.jailsonsf.pkcatchrate.engine.CatchEngine;
import com.jailsonsf.pkcatchrate.engine.CatchRequest;
import com.jailsonsf.pkcatchrate.engine.CatchResult;
import com.jailsonsf.pkcatchrate.pokemon.PokemonRepository;
import com.jailsonsf.pkcatchrate.pokemon.PokemonSpecies;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/catch-rate")
public class CatchRateController {

    private final PokemonRepository repository;
    private final CatchEngine engine;

    public CatchRateController(PokemonRepository repository, CatchEngine engine) {
        this.repository = repository;
        this.engine = engine;
    }

    @PostMapping
    public ResponseEntity<CatchRateResponse> calculate(@Valid @RequestBody CatchRateRequest request) {
        PokemonSpecies species = repository.findByName(request.name())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Unknown species: " + request.name()));
        CatchResult result = engine.compute(new CatchRequest(
                species.catchRate(),
                species.baseHp(),
                request.level(),
                request.currentHp(),
                request.ball(),
                request.status(),
                request.generation(),
                Optional.of(species.primaryType()),
                species.secondaryType(),
                request.battleContext()));
        return ResponseEntity.ok(new CatchRateResponse(
                result.probability(),
                result.guaranteed(),
                result.a(),
                result.b(),
                result.expectedBalls()));
    }

    @ExceptionHandler(BallNotAvailableException.class)
    public ResponseEntity<String> handleBallNotAvailable(BallNotAvailableException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
    }
}

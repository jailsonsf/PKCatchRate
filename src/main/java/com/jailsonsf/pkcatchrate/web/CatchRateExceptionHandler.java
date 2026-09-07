package com.jailsonsf.pkcatchrate.web;

import com.jailsonsf.pkcatchrate.exception.BallNotAvailableException;
import com.jailsonsf.pkcatchrate.exception.InvalidHpException;
import com.jailsonsf.pkcatchrate.exception.InvalidLevelException;
import com.jailsonsf.pkcatchrate.exception.UnknownPokemonException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class CatchRateExceptionHandler {

    @ExceptionHandler(UnknownPokemonException.class)
    public ResponseEntity<ApiError> handleUnknownPokemon(UnknownPokemonException exception) {
        return error(HttpStatus.NOT_FOUND, "UNKNOWN_POKEMON", exception.getMessage());
    }

    @ExceptionHandler(InvalidLevelException.class)
    public ResponseEntity<ApiError> handleInvalidLevel(InvalidLevelException exception) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_LEVEL", exception.getMessage());
    }

    @ExceptionHandler(InvalidHpException.class)
    public ResponseEntity<ApiError> handleInvalidHp(InvalidHpException exception) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_HP", exception.getMessage());
    }

    @ExceptionHandler(BallNotAvailableException.class)
    public ResponseEntity<ApiError> handleBallNotAvailable(BallNotAvailableException exception) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "BALL_NOT_AVAILABLE", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message);
    }

    private static ResponseEntity<ApiError> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ApiError(status.value(), code, message));
    }
}

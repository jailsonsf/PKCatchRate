package com.jailsonsf.pkcatchrate.web;

import com.jailsonsf.pkcatchrate.ball.PokeBall;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CatchRateRequest(
        @NotBlank String name,
        @NotNull @Min(1) @Max(100) Integer level,
        @NotNull @Min(1) Integer currentHp,
        @NotNull PokeBall ball) {
}

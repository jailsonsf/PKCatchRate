package com.jailsonsf.pkcatchrate.web;

import com.jailsonsf.pkcatchrate.ball.PokeBall;
import com.jailsonsf.pkcatchrate.battle.BattleContext;
import com.jailsonsf.pkcatchrate.generation.GameGeneration;
import com.jailsonsf.pkcatchrate.status.StatusCondition;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CatchRateRequest(
        @NotBlank String name,
        @NotNull @Min(1) @Max(100) Integer level,
        @NotNull @Min(1) Integer currentHp,
        @NotNull PokeBall ball,
        StatusCondition status,
        GameGeneration generation,
        BattleContext battleContext) {

    public CatchRateRequest {
        if (status == null) {
            status = StatusCondition.NONE;
        }
        if (generation == null) {
            generation = GameGeneration.GEN_III_IV;
        }
    }
}

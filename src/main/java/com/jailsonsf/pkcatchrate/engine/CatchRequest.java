package com.jailsonsf.pkcatchrate.engine;

import com.jailsonsf.pkcatchrate.ball.PokeBall;
import com.jailsonsf.pkcatchrate.generation.GameGeneration;
import com.jailsonsf.pkcatchrate.status.StatusCondition;

public record CatchRequest(
        int catchRate,
        int baseHp,
        int level,
        int currentHp,
        PokeBall ball,
        StatusCondition status,
        GameGeneration generation) {
}

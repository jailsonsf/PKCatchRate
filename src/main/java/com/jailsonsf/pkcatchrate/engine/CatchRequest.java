package com.jailsonsf.pkcatchrate.engine;

import com.jailsonsf.pkcatchrate.ball.PokeBall;

public record CatchRequest(
        int catchRate,
        int baseHp,
        int level,
        int currentHp,
        PokeBall ball) {
}

package com.jailsonsf.pkcatchrate.engine;

import com.jailsonsf.pkcatchrate.ball.PokeBall;
import com.jailsonsf.pkcatchrate.battle.BattleContext;
import com.jailsonsf.pkcatchrate.generation.GameGeneration;
import com.jailsonsf.pkcatchrate.status.StatusCondition;

import java.util.Optional;

public record CatchRequest(
        int catchRate,
        int baseHp,
        int level,
        int currentHp,
        PokeBall ball,
        StatusCondition status,
        GameGeneration generation,
        Optional<String> primaryType,
        Optional<String> secondaryType,
        BattleContext battleContext) {

    public CatchRequest(int catchRate, int baseHp, int level, int currentHp, PokeBall ball,
                        StatusCondition status, GameGeneration generation) {
        this(catchRate, baseHp, level, currentHp, ball, status, generation,
                Optional.empty(), Optional.empty(), null);
    }
}

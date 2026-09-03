package com.jailsonsf.pkcatchrate.ball;

import com.jailsonsf.pkcatchrate.battle.BattleContext;
import com.jailsonsf.pkcatchrate.generation.GameGeneration;
import com.jailsonsf.pkcatchrate.status.StatusCondition;

import java.util.Optional;

public class PokeBallRates {

    private static final double TIMER_STEP = 1229.0 / 4096.0;

    public boolean isAvailable(PokeBall ball, GameGeneration generation) {
        return switch (ball) {
            case POKE, GREAT, ULTRA, MASTER, TIMER, QUICK, REPEAT, NET, DIVE, NEST, DUSK -> true;
            case DREAM -> generation == GameGeneration.GEN_VI_PLUS;
            case LURE, BEAST, HEAVY, FAST, LEVEL, LOVE, MOON -> false;
        };
    }

    public double multiplier(PokeBall ball, GameGeneration generation, BattleContext battleContext,
                             int level, StatusCondition status,
                             Optional<String> primaryType, Optional<String> secondaryType) {
        return switch (ball) {
            case POKE -> 1.0;
            case GREAT -> 1.5;
            case ULTRA -> 2.0;
            case MASTER -> 1.0;
            case TIMER -> timerMultiplier(generation, battleContext);
            case QUICK -> quickMultiplier(generation, battleContext);
            case REPEAT -> repeatMultiplier(generation, battleContext);
            case NET -> netMultiplier(generation, primaryType, secondaryType);
            case DIVE -> diveMultiplier(battleContext);
            case NEST -> nestMultiplier(generation, level);
            case DUSK -> duskMultiplier(generation, battleContext);
            case DREAM -> dreamMultiplier(status);
            case LURE, BEAST, HEAVY, FAST, LEVEL, LOVE, MOON -> 1.0;
        };
    }

    private static double timerMultiplier(GameGeneration generation, BattleContext battleContext) {
        int turns = battleContext == null ? 0 : battleContext.turnsElapsed();
        return switch (generation) {
            case GEN_III_IV -> Math.min((turns + 10) / 10.0, 4.0);
            case GEN_V, GEN_VI_PLUS -> Math.min(1.0 + turns * TIMER_STEP, 4.0);
        };
    }

    private static double quickMultiplier(GameGeneration generation, BattleContext battleContext) {
        if (battleContext == null || !battleContext.isFirstTurn()) {
            return 1.0;
        }
        return switch (generation) {
            case GEN_III_IV -> 4.0;
            case GEN_V, GEN_VI_PLUS -> 5.0;
        };
    }

    private static double repeatMultiplier(GameGeneration generation, BattleContext battleContext) {
        if (battleContext == null || !battleContext.isAlreadyCaught()) {
            return 1.0;
        }
        return switch (generation) {
            case GEN_III_IV, GEN_V -> 3.0;
            case GEN_VI_PLUS -> 3.5;
        };
    }

    private static double netMultiplier(GameGeneration generation,
                                        Optional<String> primaryType, Optional<String> secondaryType) {
        boolean bugOrWater = isType(primaryType, "bug") || isType(primaryType, "water")
                || isType(secondaryType, "bug") || isType(secondaryType, "water");
        if (!bugOrWater) {
            return 1.0;
        }
        return switch (generation) {
            case GEN_III_IV, GEN_V -> 3.0;
            case GEN_VI_PLUS -> 3.5;
        };
    }

    private static double diveMultiplier(BattleContext battleContext) {
        if (battleContext == null || (!battleContext.onWater() && !battleContext.fromFishing())) {
            return 1.0;
        }
        return 3.5;
    }

    private static double nestMultiplier(GameGeneration generation, int level) {
        return switch (generation) {
            case GEN_III_IV -> Math.max((40 - level) / 10.0, 1.0);
            case GEN_V -> Math.max(Math.floor((41 - level) * 4096.0 / 10.0) / 4096.0, 1.0);
            case GEN_VI_PLUS -> level >= 30
                    ? 1.0
                    : Math.max(Math.floor((41 - level) * 4096.0 / 10.0) / 4096.0, 1.0);
        };
    }

    private static double duskMultiplier(GameGeneration generation, BattleContext battleContext) {
        if (battleContext == null || !battleContext.inCaveOrNight()) {
            return 1.0;
        }
        return switch (generation) {
            case GEN_III_IV, GEN_V -> 3.5;
            case GEN_VI_PLUS -> 3.0;
        };
    }

    private static double dreamMultiplier(StatusCondition status) {
        return status == StatusCondition.SLEEP ? 4.0 : 1.0;
    }

    private static boolean isType(Optional<String> type, String expected) {
        return type.filter(value -> value.equalsIgnoreCase(expected)).isPresent();
    }
}

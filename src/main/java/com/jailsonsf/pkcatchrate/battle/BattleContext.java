package com.jailsonsf.pkcatchrate.battle;

public record BattleContext(
        int turnsElapsed,
        boolean isFirstTurn,
        boolean isAlreadyCaught,
        boolean fromFishing,
        boolean onWater,
        boolean inCaveOrNight) {
}

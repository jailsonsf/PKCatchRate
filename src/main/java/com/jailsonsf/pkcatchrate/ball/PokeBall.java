package com.jailsonsf.pkcatchrate.ball;

public enum PokeBall {

    POKE,
    GREAT,
    ULTRA,
    MASTER,
    TIMER,
    QUICK,
    REPEAT,
    NET,
    DIVE,
    NEST,
    DUSK,
    DREAM,
    LURE,
    BEAST,
    HEAVY,
    FAST,
    LEVEL,
    LOVE,
    MOON;

    public boolean isGuaranteed() {
        return this == MASTER;
    }
}

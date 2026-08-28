package com.jailsonsf.pkcatchrate.ball;

public enum PokeBall {

    POKE(1.0),
    GREAT(1.5),
    ULTRA(2.0),
    MASTER(1.0);

    private final double multiplier;

    PokeBall(double multiplier) {
        this.multiplier = multiplier;
    }

    public double multiplier() {
        return multiplier;
    }

    public boolean isGuaranteed() {
        return this == MASTER;
    }
}

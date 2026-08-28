package com.jailsonsf.pkcatchrate.engine;

public record CatchResult(
        double probability,
        boolean guaranteed,
        int a,
        int b,
        double expectedBalls) {
}

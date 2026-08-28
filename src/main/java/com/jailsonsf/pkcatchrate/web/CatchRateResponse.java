package com.jailsonsf.pkcatchrate.web;

public record CatchRateResponse(
        double probability,
        boolean guaranteed,
        int a,
        int b,
        double expectedBalls) {
}

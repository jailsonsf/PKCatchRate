package com.jailsonsf.pkcatchrate.exception;

import com.jailsonsf.pkcatchrate.ball.PokeBall;
import com.jailsonsf.pkcatchrate.generation.GameGeneration;

public class BallNotAvailableException extends CatchRateException {

    public BallNotAvailableException(PokeBall ball, GameGeneration generation) {
        super("Ball " + ball + " is not available in " + generation);
    }
}

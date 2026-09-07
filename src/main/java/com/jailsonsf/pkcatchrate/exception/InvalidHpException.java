package com.jailsonsf.pkcatchrate.exception;

public class InvalidHpException extends CatchRateException {

    public InvalidHpException(int currentHp) {
        super("Current HP must be at least 1 but was " + currentHp);
    }
}

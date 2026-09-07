package com.jailsonsf.pkcatchrate.exception;

public class InvalidLevelException extends CatchRateException {

    public InvalidLevelException(int level) {
        super("Level must be between 1 and 100 but was " + level);
    }
}

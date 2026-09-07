package com.jailsonsf.pkcatchrate.exception;

public abstract class CatchRateException extends RuntimeException {

    protected CatchRateException(String message) {
        super(message);
    }
}

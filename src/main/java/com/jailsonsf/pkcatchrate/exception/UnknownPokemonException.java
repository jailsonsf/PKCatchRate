package com.jailsonsf.pkcatchrate.exception;

public class UnknownPokemonException extends CatchRateException {

    public UnknownPokemonException(String name) {
        super("Unknown species: " + name);
    }
}

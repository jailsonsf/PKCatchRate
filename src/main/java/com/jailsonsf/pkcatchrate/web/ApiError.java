package com.jailsonsf.pkcatchrate.web;

public record ApiError(
        int status,
        String error,
        String message) {
}

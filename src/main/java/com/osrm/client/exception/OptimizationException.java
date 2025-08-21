package com.osrm.client.exception;

public abstract class OptimizationException extends RuntimeException {
    public OptimizationException(String message) {
        super(message);
    }
}

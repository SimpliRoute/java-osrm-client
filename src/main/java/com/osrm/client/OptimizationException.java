package com.osrm.client;

public abstract class OptimizationException extends RuntimeException {
    public OptimizationException(String message) {
        super(message);
    }
}

package com.osrm.client.exception;

public class DistanceMatrixResponseException extends RuntimeException {
  public DistanceMatrixResponseException(String message) {
    super(message);
  }
}

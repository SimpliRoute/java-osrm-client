package com.osrm.client;

import com.google.gson.Gson;

public class UnsuccessfulResponse {
  private String message;
  private String statusCode;
  private String error;

  public UnsuccessfulResponse(String message, String statusCode, String error) {
    this.message = message;
    this.statusCode = statusCode;
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public String getStatusCode() {
    return statusCode;
  }

  public String getError() {
    return error;
  }

  public static UnsuccessfulResponse fromJSON (String json) {
    return new Gson().fromJson(json, UnsuccessfulResponse.class);
  }
}

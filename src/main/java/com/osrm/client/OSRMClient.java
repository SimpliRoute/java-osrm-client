package com.osrm.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OSRMClient {
  private final String uri;

  public OSRMClient(String uri) throws EmptyUrlException {
    if (uri != null || !uri.isEmpty()) {
      this.uri = uri;
    } else {
      throw new EmptyUrlException("OSRMClient Constructor requires a OSRM http url");
    }
  }


  public OSRMDistanceResponse getDistanceMatrix(List<GeoLocation> locations, double speedRate, String country,
                                                String token, String profile,
                                                String startTime) throws OptimizationDistanceMatrixException {
    Builder requestBuilder = new Builder();

    requestBuilder.readTimeout(900000, TimeUnit.MILLISECONDS);
    requestBuilder.writeTimeout(900000, TimeUnit.MILLISECONDS);

    OkHttpClient client = requestBuilder.build();

    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

    List<String> locationsCollection = new ArrayList<>();


    for (GeoLocation geoloc : locations) {
      locationsCollection.add(geoloc.getLatLongString());
    }

    String paramsString = String.join("&loc=", locationsCollection);

    paramsString += "&speedRate=" + speedRate;
    paramsString += "&country=" + country;

    if (startTime != null && !startTime.isEmpty()) {
      paramsString += "&start_time=" + startTime;
    }

    RequestBody body = RequestBody.create(mediaType, "loc=" + paramsString);

    Request request = new Request.Builder()
        .url(this.uri + "/table/" + profile)
        .post(body)
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .addHeader("Authorization", token)
        .build();

    Response response;
    try {
      response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        return OSRMDistanceResponse.fromJSON(response.body().string());
      }
    } catch (Exception e) {
      System.out.print(e.getMessage());
      throw new OptimizationDistanceMatrixException("Error while connecting to OSRM Server");
    }

    UnsuccessfulResponse unsuccessfulResponse = this.getUnsuccessfulResponse(response);
    if (unsuccessfulResponse != null && unsuccessfulResponse.getMessage() != null) {
      throw new DistanceMatrixResponseException("OSRM Error: " + unsuccessfulResponse.getMessage());
    }

    throw new DistanceMatrixResponseException("OSRM Error: " + response);
  }

  private UnsuccessfulResponse getUnsuccessfulResponse(Response response){
    try {
      if (response.body() != null) {
                  String bodyResponse = response.body().string();
        UnsuccessfulResponse    unsuccessfulResponse = UnsuccessfulResponse.fromJSON(bodyResponse);
        if(unsuccessfulResponse.getMessage() == null){
          return new UnsuccessfulResponse(bodyResponse,String.valueOf(response.code()),"");
        }
      }
    } catch (Exception e) {
      System.out.print("Error getUnsuccessfulResponse.fromJSON: " + e.getMessage());
                  }
    return null;
  }


}

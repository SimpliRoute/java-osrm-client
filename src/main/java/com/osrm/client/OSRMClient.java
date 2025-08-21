package com.osrm.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.osrm.client.exception.DistanceMatrixResponseException;
import com.osrm.client.exception.EmptyUrlException;
import com.osrm.client.exception.OptimizationDistanceMatrixException;
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
                                                String options) throws OptimizationDistanceMatrixException {
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
    paramsString += encodeJsonToUrlParams(options);

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

  public static String encodeJsonToUrlParams(String options) {
    ObjectMapper objectMapper = new ObjectMapper();
    StringBuilder urlParams = new StringBuilder();
    try {
      Map<String, Object> map = objectMapper.readValue(options, Map.class);

      for (Map.Entry<String, Object> entry : map.entrySet()) {
        String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString());
        String value = URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8.toString());
        urlParams.append("&").append(key).append("=").append(value);
      }
    }catch (Exception e) {
      System.out.print("Error getUnsuccessfulResponse.fromJSON: " + e.getMessage());
    }
    return urlParams.toString();
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

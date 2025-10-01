package com.osrm.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.osrm.client.exception.EmptyUrlException;
import com.osrm.client.exception.OSRMClientException;
import com.osrm.client.request.CostMatricesRequest;
import com.osrm.client.request.GeoLocation;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OSRMClient implements CostService {
  private final String uri;
  private static final String ENDPOINT_V2_TABLE = "/v2/table/";

  public OSRMClient(String uri) throws EmptyUrlException {
    if (!stringHasValue(uri)) {
      throw new EmptyUrlException("OSRMClient Constructor requires a OSRM http url");
    }

    this.uri = uri;
  }

  @Override
  public CostMatrices getCostMatrices(CostMatricesRequest request) {
    Builder requestBuilder = new Builder();

    requestBuilder.readTimeout(900000, TimeUnit.MILLISECONDS);
    requestBuilder.writeTimeout(900000, TimeUnit.MILLISECONDS);

    OkHttpClient client = requestBuilder.build();

    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

    List<String> locationsCollection = new ArrayList<>();

    for (GeoLocation geolocation : request.getLocations()) {
      locationsCollection.add(geolocation.getLatLongString());
    }

    String paramsString = String.join("&loc=", locationsCollection);

    paramsString = addParamString(paramsString, "speedRate", Double.toString(request.getSpeedRate()));
    paramsString = addParamString(paramsString, "country", request.getCountry());
    paramsString = addParamString(paramsString, "start_time", request.getStartTime());
    paramsString = addParamString(paramsString, "vehicleSubType", request.getVehicleSubType());
    paramsString = addParamString(paramsString, "restriction", request.getRestrictionOption());

    final String metricsParam = request.isReturnDistanceMatrix() ? "time,distance" : "time";

    paramsString = addParamString(paramsString, "metrics", metricsParam);

    for (Map.Entry<String, Object> paramEntry : request.getCustomParameters().entrySet()) {
      paramsString = addParamString(paramsString, paramEntry.getKey(), paramEntry.getValue().toString());
    }

    RequestBody body = RequestBody.create(mediaType, "loc=" + paramsString);

    Request osrmRequest = new Request.Builder()
            .url(this.uri + ENDPOINT_V2_TABLE + request.getProfile())
            .post(body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Authorization", request.getToken())
            .build();

    try {
      Response response = client.newCall(osrmRequest).execute();

      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(response.body().string(), CostMatrices.class);
    } catch (Exception e) {
      throw new OSRMClientException("Error while connecting to OSRM Server");
    }
  }

  private String addParamString(String paramString, String key, String value) {
    return stringHasValue(value) ? paramString.concat("&" + key + "=" + value) : paramString;
  }

  private boolean stringHasValue(String string) {
    return (string != null) && (!string.equals(""));
  }
}

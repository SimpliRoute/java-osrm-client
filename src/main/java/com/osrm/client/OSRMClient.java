package com.osrm.client;

import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient.Builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class OSRMClient {
    private final String uri;

    public OSRMClient(String uri) throws EmptyUrlException {
        if (uri != null || !uri.isEmpty()) {
            this.uri = uri;
        }
        else {
            throw new EmptyUrlException("OSRMClient Constructor requires a OSRM http url");
        }
    }


    public OSRMDistanceResponse getDistanceMatrix(List<GeoLocation> locations, double speedRate) throws OptimizationDistanceMatrixException {
        OSRMDistanceResponse osrmDistanceResponse;

        Builder requestBuilder = new Builder();

        requestBuilder.readTimeout(900000, TimeUnit.MILLISECONDS);
        requestBuilder.writeTimeout(900000, TimeUnit.MILLISECONDS);

        OkHttpClient client = requestBuilder.build();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        List<String> locationsCollection = new ArrayList<>();


        for (GeoLocation geoloc: locations) {
            locationsCollection.add(geoloc.getLatLongString());
        }

        String paramsString = String.join("&loc=", locationsCollection);

        paramsString += "&speedRate=" + speedRate;

        RequestBody body = RequestBody.create(mediaType, "loc=" + paramsString);

        Request request = new Request.Builder()
                .url(this.uri + "/table")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try {
            Response response = client.newCall(request).execute();
            osrmDistanceResponse = OSRMDistanceResponse.fromJSON(response.body().string());
        }
        catch(IOException e) {
            System.out.print(e.getMessage());
            throw new OptimizationDistanceMatrixException("Error while connecting to osrm server");
        }

        return osrmDistanceResponse;
    }
}

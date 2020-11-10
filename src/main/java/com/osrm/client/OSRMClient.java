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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class OSRMClient {
    private static final Logger log = LogManager.getLogger(OSRMClient.class);
    
    private final String uri;

    public OSRMClient(String uri) throws EmptyUrlException {
        if (uri != null || !uri.isEmpty()) {
            this.uri = uri;
        }
        else {
            throw new EmptyUrlException("OSRMClient Constructor requires a OSRM http url");
        }
    }


    public OSRMDistanceResponse getDistanceMatrix(List<GeoLocation> locations, double speedRate, String country, String token) throws OptimizationDistanceMatrixException {
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
        paramsString += "&country=" + country;

        RequestBody body = RequestBody.create(mediaType, "loc=" + paramsString);

        Request request = new Request.Builder()
                .url(this.uri + "/table")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", token)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String matrix = response.body().string();
            System.out.print("matrix:"+matrix);
            log.info("matrix:"+matrix);
            osrmDistanceResponse = OSRMDistanceResponse.fromJSON(matrix);
        }
        catch(IOException e) {
            System.out.print(e.getMessage());
            log.error("osrm-client IOException:", e);
            throw new OptimizationDistanceMatrixException("Error while connecting to osrm server");
        }
        catch (Exception e) {
            System.out.print("Error osrm-client: ");
            System.out.print(e);
            log.error("Error osrm-client:", e);
            throw new OptimizationDistanceMatrixException("Error request osrm");
        }

        return osrmDistanceResponse;
    }
}

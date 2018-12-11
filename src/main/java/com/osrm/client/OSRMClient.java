package com.osrm.client;

import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// import org.apache.http.util.EntityUtils;

/*import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
*/


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


    public OSRMDistanceResponse getDistanceMatrix(List<GeoLocation> locations) throws OptimizationDistanceMatrixException {
        OSRMDistanceResponse osrmDistanceResponse;

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        List<String> locationsCollection = new ArrayList<>();


        for (GeoLocation geoloc: locations) {
            locationsCollection.add(geoloc.getLatLongString());
        }

        String locationsString = String.join("&loc=", locationsCollection);

        RequestBody body = RequestBody.create(mediaType, "loc="+locationsString);

        Request request = new Request.Builder()
                .url(this.uri + "/table")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try {
            Response response = client.newCall(request).execute();
            osrmDistanceResponse = OSRMDistanceResponse.fromJSON(response.body().string());
            // osrmDistanceResponse = OSRMDistanceResponse.fromJSON(EntityUtils.toString(response.getEntity()));
        }
        catch(IOException e) {
            System.out.print(e.getMessage());
            throw new OptimizationDistanceMatrixException("Error while connecting to osrm server");
        }


        // HttpClient http = HttpClientBuilder.create().build();

        // RequestBuilder builder = RequestBuilder.post(this.uri + "/table");
        // builder.addHeader("Content-Type", "charset=UTF-8");

        /*
        for (GeoLocation geoloc: locations) {
            builder.addParameter("loc", geoloc.getLatLongString());
        }

        try {
            HttpResponse response = http.execute(builder.build());
            osrmDistanceResponse = OSRMDistanceResponse.fromJSON(EntityUtils.toString(response.getEntity()));
        }
        catch(IOException e) {
            System.out.print(e.getMessage());
            throw new OptimizationDistanceMatrixException("Error while connecting to osrm server");
        }
        */
        return osrmDistanceResponse;
    }
}

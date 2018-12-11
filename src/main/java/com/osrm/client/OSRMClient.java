package com.osrm.client;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


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
        HttpClient http = HttpClientBuilder.create().build();

        RequestBuilder builder = RequestBuilder.post(this.uri + "/table");

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
        return osrmDistanceResponse;
    }
}

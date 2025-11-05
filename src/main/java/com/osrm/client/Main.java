package com.osrm.client;
import com.osrm.client.exception.EmptyUrlException;
import com.osrm.client.request.CostMatricesRequest;
import com.osrm.client.request.GeoLocation;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws EmptyUrlException {

        try {
            OSRMClient client = new OSRMClient("http://0.0.0.0:8080");
            List<GeoLocation> locations = new ArrayList<>();

            GeoLocation geo1 = new GeoLocation(42.5434488, 1.4949332);
            GeoLocation geo2 = new GeoLocation(42.5434488, 1.395);

            locations.add(geo1);
            locations.add(geo2);

            // low fmv
            double speedRate = 2;
            String country = "AD";

            // token
            String token = "Token TUTOKEN";

            String profile = "car";

            CostMatricesRequest request = CostMatricesRequest.builder()
                    .speedRate(speedRate)
                    .country(country)
                    .locations(locations)
                    .token(token)
                    .profile(profile)
                    .returnDistanceMatrix(true)
                    .startTime("2025-11-12T09:00:00+00:00")
                    .customParameters(Collections.emptyMap())
                    .build();

            CostMatrices matrices = client.getCostMatrices(request);

            System.out.println(matrices);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("End with errors.");
        }
    }
}

package com.osrm.client;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws EmptyUrlException {

        try {
            OSRMClient client = new OSRMClient("http://localhost:8008");
            List<GeoLocation> locations = new ArrayList<>();

            GeoLocation geo1 = new GeoLocation(-33.416943, -70.60952);
            GeoLocation geo2 = new GeoLocation(-33.416943, -70.60952);
            GeoLocation geo3 = new GeoLocation(-33.4445755, -70.6404943);
            GeoLocation geo4 = new GeoLocation(-33.4457167, -70.61926449999999);

            locations.add(geo1);
            locations.add(geo2);
            locations.add(geo3);
            locations.add(geo4);

            // low fmv
            double speedRate = 2;
            String country = "CL";

            // token
            String token = "None";

            String profile = "car";

            OSRMDistanceResponse response = client.getDistanceMatrix(locations, speedRate, country, token, profile,
                                                                     null);

            System.out.println(response);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("End with errors.");
        }
    }
}

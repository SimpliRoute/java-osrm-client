package com.osrm.client;

import java.util.List;

public interface DistanceService {
    DistanceMatrix buildDistanceMatrix(List<GeoLocation> coordinates);
}
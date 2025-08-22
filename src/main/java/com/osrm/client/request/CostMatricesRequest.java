package com.osrm.client.request;

import lombok.Builder;

import java.util.List;

@Builder
public class CostMatricesRequest {
  private final List<GeoLocation> locations;
  private final double speedRate;
  private final String country;
  private final String token;
  private final String profile;
  private final String options;
}

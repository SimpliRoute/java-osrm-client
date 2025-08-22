package com.osrm.client.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class CostMatricesRequest {
  private final List<GeoLocation> locations;
  private final double speedRate;
  private final String country;
  private final String token;
  private final String profile;
  private final String startTime;
  private final String vehicleSubType;
  private final String restrictionOption;
  private final String metrics;
}

package com.osrm.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CostMatrices {
  private final CostMatrix timeMatrix;
  private final CostMatrix distanceMatrix;
}

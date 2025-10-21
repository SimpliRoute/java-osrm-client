package com.osrm.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CostMatrices {
  private CostMatrix timeMatrix;
  private CostMatrix distanceMatrix;
}

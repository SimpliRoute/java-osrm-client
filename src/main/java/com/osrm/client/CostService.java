package com.osrm.client;

import com.osrm.client.request.CostMatricesRequest;


public interface CostService {
  CostMatrices getCostMatrices(CostMatricesRequest request);
}

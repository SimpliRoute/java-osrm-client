package com.osrm.client;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class OSRMDistanceResponse implements DistanceResponse {

    @SerializedName("matrix")
    private final List<List<Integer>> distanceTable;

    public OSRMDistanceResponse(List<List<Integer>> distanceTable) {
        this.distanceTable = distanceTable;
    }

    private List<List<Integer>> getDistanceTable() {
        return distanceTable;
    }

    public DistanceMatrix toDistanceMatrix() {
        DistanceMatrix matrix = new DistanceMatrix(getDistanceTable().size());
        int i = 0;
        for (List<Integer> rows : this.getDistanceTable()) {
            int j = 0;
            for (Integer col : rows) {
                matrix.setValueAtCoord(i, j, col);
                j++;
            }
            i++;
        }
        return matrix;
    }


    public static OSRMDistanceResponse fromJSON (String json) {
        return new Gson().fromJson(json, OSRMDistanceResponse.class);
    }
}

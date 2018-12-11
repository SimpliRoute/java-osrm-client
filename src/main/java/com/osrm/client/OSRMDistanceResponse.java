package com.osrm.client;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class OSRMDistanceResponse implements DistanceResponse {

    @SerializedName("distance_table")
    private final List<List<Integer>> distanceTable;

    public OSRMDistanceResponse(List<List<Integer>> distanceTable) {
        this.distanceTable = distanceTable;
    }

    private List<List<Integer>> getDistanceTable() {
        return distanceTable;
    }

    public DistanceMatrix toDistanceMatrix() {
        DistanceMatrix matrix = new DistanceMatrix(this.getDistanceTable().size());
        int x = 0;
        for (List<Integer> rows: this.getDistanceTable()) {
            int y = 0;
            for (Integer col: rows) {
                matrix.setValueAtCoord(x, y, col);
                y++;
            }
            x++;
        }
        return matrix;
    }

    public static OSRMDistanceResponse fromJSON (String json) {
        return new Gson().fromJson(json, OSRMDistanceResponse.class);
    }
}

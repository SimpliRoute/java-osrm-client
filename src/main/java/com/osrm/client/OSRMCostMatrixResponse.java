package com.osrm.client;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
class OSRMCostMatrixResponse implements CostMatrixResponse {
    @SerializedName("matrix")
    private final List<List<Integer>> costTable;

    public CostMatrix toCostMatrix() {
        CostMatrix matrix = new CostMatrix(getCostTable().size());
        int i = 0;
        for (List<Integer> rows : this.getCostTable()) {
            int j = 0;
            for (Integer col : rows) {
                matrix.setValueAtCoord(i, j, col);
                j++;
            }
            i++;
        }
        return matrix;
    }

    public static OSRMCostMatrixResponse fromJSON (String json) {
        return new Gson().fromJson(json, OSRMCostMatrixResponse.class);
    }
}

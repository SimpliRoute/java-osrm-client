package com.osrm.client;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CostMatrix {
    private int[][] matrix;

    public double getValueAtCoord(int x, int y) {
        return this.matrix[x][y];
    }
    public void setValueAtCoord(int x, int y, int value) {
        this.matrix[x][y] = value;
    }

    public List<List<Float>> asList() {
        List<List<Float>> list =  new ArrayList<>();
        for (int x = 0; x < matrix.length; x++) {
            List<Float> row = new ArrayList<>();
            for (int y = 0; y < matrix.length; y++) {
                float cost = Float.MAX_VALUE;
                try {
                    cost = Double.valueOf(matrix[x][y]).floatValue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                row.add(cost);
            }
            list.add(row);
        }
        return list;
    }
}

package com.osrm.client;

import java.util.ArrayList;
import java.util.List;

public class CostMatrix {
    private final int[][] matrix;

    public CostMatrix(int size) {
        this.matrix = new int[size][size];
    }

    public void setValueAtCoord(int x, int y, int value) {
        this.matrix[x][y] = value;
    }

    public int getValueAtCoord(int x, int y) {
        return this.matrix[x][y];
    }

    public List<List<Float>> asList() {
        List<List<Float>> list =  new ArrayList<>();
        for (int x = 0; x < matrix.length; x++) {
            List<Float> row = new ArrayList<>();
            for (int y = 0; y < matrix.length; y++) {
                Float cost = Float.MAX_VALUE;
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

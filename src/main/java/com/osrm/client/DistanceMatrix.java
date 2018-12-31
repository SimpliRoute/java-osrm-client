package com.osrm.client;

import java.util.ArrayList;
import java.util.List;

public class DistanceMatrix {
    private int[][] matrix;

    public DistanceMatrix(int size) {
        this.matrix = new int[size][size];
    }

    public void setValueAtCoord(int x, int y, int value) {
        this.matrix[x][y] = value;
    }

    public int getValueAtCoord(int x, int y) {
        return this.matrix[x][y];
    }

    public List<List<Float>> asList(double fmv) {
        List<List<Float>> list =  new ArrayList<>();
        for (int[] x : matrix) {
            List<Float> row = new ArrayList<>();
            for (int y: x) {
                float distance = Float.MAX_VALUE;
                try {
                    distance = Double.valueOf(y * fmv).floatValue();
                }
                catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
                row.add(distance);
            }
            list.add(row);
        }
        return list;
    }
}

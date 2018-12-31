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
        for (int x = 0; x < matrix.length; x++) {
            List<Float> row = new ArrayList<>();
            for (int y = 0; y < matrix.length; y++) {
                Float distance = Float.MAX_VALUE;
                try {
                    distance = Double.valueOf(matrix[x][y] * fmv).floatValue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                row.add(distance);
            }
            list.add(row);
        }
        return list;
    }
}

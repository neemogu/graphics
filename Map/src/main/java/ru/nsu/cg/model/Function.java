package ru.nsu.cg.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public class Function {
    public static final int minGridSize = 3;
    public static final int maxGridSize = 1000;
    @Getter
    private double startX = -5.0;
    private double savedStartX = startX;
    @Getter
    private double endX = 5.0;
    private double savedEndX = endX;
    @Getter
    private double startY = -5.0;
    private double savedStartY = startY;
    @Getter
    private double endY = 5.0;
    private double savedEndY = endY;
    @Getter
    private int gridWidth = 30;
    private int savedGridWidth = gridWidth;
    @Getter
    private int gridHeight = 30;
    private int savedGridHeight = gridHeight;

    public double calculate(double x, double y) {
        return Math.sin(y) * Math.cos(x);
    }

    public double[][] getCalculatedMatrix() {
        double[][] result = new double[gridHeight][gridWidth];
        double deltaX = (endX - startX) / (gridWidth - 1);
        double deltaY = (endY - startY) / (gridHeight - 1);
        for (int i = 0; i < gridHeight; ++i) {
            for (int j = 0; j < gridWidth; ++j) {
                result[i][j] = calculate(startX + j * deltaX, startY + i * deltaY);
            }
        }
        return result;
    }

    public void saveParameters() {
        savedEndX = endX;
        savedEndY = endY;
        savedStartX = startX;
        savedStartY = startY;
        savedGridHeight = gridHeight;
        savedGridWidth = gridWidth;
    }

    public void recoverParameters() {
        endX = savedEndX;
        endY = savedEndY;
        startX = savedStartX;
        startY = savedStartY;
        gridHeight = savedGridHeight;
        gridWidth = savedGridWidth;
    }

    public void setGridSize(int gridWidth, int gridHeight) throws IllegalArgumentException {
        if (gridWidth >= minGridSize && gridWidth <= maxGridSize &&
                gridHeight >= minGridSize && gridHeight <= maxGridSize) {
            this.gridWidth = gridWidth;
            this.gridHeight = gridHeight;
        } else {
            throw new IllegalArgumentException("Grid size must be between " + minGridSize + " and " + maxGridSize);
        }
    }

    public void setBoundsX(double startX, double endX) throws IllegalArgumentException {
        if (startX <= endX) {
            this.startX = startX;
            this.endX = endX;
        } else {
            throw new IllegalArgumentException("Start X must be less or equal to End X");
        }
    }

    public void setBoundsY(double startY, double endY) throws IllegalArgumentException {
        if (startY <= endY) {
            this.startY = startY;
            this.endY = endY;
        } else {
            throw new IllegalArgumentException("Start Y must be less or equal to End Y");
        }
    }

    public static double gridMin(double[][] grid) {
        double result = Double.MAX_VALUE;
        for (double[] row : grid) {
            for (double v : row) {
                if (v < result) {
                    result = v;
                }
            }
        }
        return result;
    }

    public static double gridMax(double[][] grid) {
        double result = Double.MIN_VALUE;
        for (double[] row : grid) {
            for (double v : row) {
                if (v > result) {
                    result = v;
                }
            }
        }
        return result;
    }
}

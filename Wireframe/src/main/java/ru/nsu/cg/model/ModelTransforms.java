package ru.nsu.cg.model;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class ModelTransforms {
    public static RealMatrix getStartMatrix() {
        return new DiagonalMatrix(new double[] {1, 1, 1, 1});
    }

    public static RealMatrix yaw(RealMatrix matrix, double angleDegrees) {
        double radians = Math.toRadians(angleDegrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double[][] matrixData = new double[][] {
                {cos, 0, sin, 0},
                {0, 1, 0, 0},
                {-sin, 0, cos, 0},
                {0, 0, 0, 1}
        };
        RealMatrix yawMatrix = new Array2DRowRealMatrix(matrixData);
        return yawMatrix.multiply(matrix);
    }

    public static RealMatrix pitch(RealMatrix matrix, double angleDegrees) {
        double radians = Math.toRadians(angleDegrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double[][] matrixData = new double[][] {
                {1, 0, 0, 0},
                {0, cos, -sin, 0},
                {0, sin, cos, 0},
                {0, 0, 0, 1}
        };
        RealMatrix pitchMatrix = new Array2DRowRealMatrix(matrixData);
        return pitchMatrix.multiply(matrix);
    }

    public static RealMatrix roll(RealMatrix matrix, double angleDegrees) {
        double radians = Math.toRadians(angleDegrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double[][] matrixData = new double[][] {
                {cos, -sin, 0, 0},
                {sin, cos, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        RealMatrix pitchMatrix = new Array2DRowRealMatrix(matrixData);
        return pitchMatrix.multiply(matrix);
    }
}

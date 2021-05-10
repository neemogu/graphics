package ru.nsu.cg.model;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import static ru.nsu.cg.model.Graphics2D.getTParams;

public class Graphics3D {
    private static void vertexShader(Vector3D[][] vertices, RealMatrix modelMatrix, RealMatrix viewMatrix,
                                    RealMatrix projectionMatrix, double screenWidth, double screenHeight) {
        RealMatrix finalMatrix = projectionMatrix.multiply(viewMatrix).multiply(modelMatrix);
        for (int i = 0; i < vertices.length; ++i) {
            for (int j = 0; j < vertices[0].length; ++j) {
                Vector3D vertex = vertices[i][j];
                RealMatrix vertexVector = new Array2DRowRealMatrix(new double[][]{
                        {vertex.getX()},
                        {vertex.getY()},
                        {vertex.getZ()},
                        {1.0}
                });
                RealVector resultVector = finalMatrix.multiply(vertexVector).getColumnVector(0);
                vertices[i][j] = new Vector3D(
                        (resultVector.getEntry(0) / resultVector.getEntry(3) + 1.0) / 2 * (screenWidth - 1),
                        (resultVector.getEntry(1) / resultVector.getEntry(3) + 1.0) / 2 * (screenHeight - 1),
                        (resultVector.getEntry(2) / resultVector.getEntry(3) + 1.0) / 2
                );
            }
        }
    }

    private static Vector3D[][] getRotatedVertices(Point[] vertices, int n, int k) {
        RealMatrix[] rotationMatrices = new RealMatrix[(k + 1) * n - 1];
        for (int i = 0; i < rotationMatrices.length; ++i) {
            rotationMatrices[i] = ModelTransforms.pitch(ModelTransforms.getStartMatrix(),
                    (i + 1) * 360.0 / ((k + 1) * n));
        }
        Vector3D[][] rotatedVertices = new Vector3D[(k + 1) * n][vertices.length];
        for (int i = 0; i < vertices.length; ++i) {
            Point vertex = vertices[i];
            RealMatrix vertexVector = new Array2DRowRealMatrix(new double[][]{
                    {vertex.getX()},
                    {vertex.getY()},
                    {0.0},
                    {1.0}
            });
            for (int j = 0; j < rotatedVertices.length; ++j) {
                RealVector resultVector;
                if (j != 0) {
                    resultVector = rotationMatrices[j - 1].multiply(vertexVector).getColumnVector(0);
                } else {
                    resultVector = vertexVector.getColumnVector(0);
                }
                rotatedVertices[j][i] = new Vector3D(
                        resultVector.getEntry(0) / resultVector.getEntry(3),
                        resultVector.getEntry(1) / resultVector.getEntry(3),
                        resultVector.getEntry(2) / resultVector.getEntry(3)
                );
            }
        }
        return rotatedVertices;
    }

    private static double length(Point p1, Point p2) {
        return Math.sqrt(
                Math.pow(p2.getX() - p1.getX(), 2) +
                        Math.pow(p2.getY() - p1.getY(), 2)
        );
    }

    private static double countLength(Point[] curvePoints, double[] cumulativeLengths) {
        if (cumulativeLengths.length != curvePoints.length) {
            throw new IllegalArgumentException();
        }
        double result = 0;
        for (int i = 0; i < curvePoints.length - 1; ++i) {
            double nextLength = length(curvePoints[i + 1], curvePoints[i]);
            cumulativeLengths[i] = result;
            result += nextLength;
        }
        cumulativeLengths[curvePoints.length - 1] = result;
        return result;
    }

    private static Point lengthInterpolate(Point p1, Point p2, double length, double interpLength) {
        return new Point(
                (float)(p1.getX() + (p2.getX() - p1.getX()) * interpLength / length),
                (float)(p1.getY() + (p2.getY() - p1.getY()) * interpLength / length)
                );
    }

    private static Point[] getMGridPoints(Point[] curvePoints, int m) {
        Point[] gridPoints = new Point[m + 1];
        double[] cumulativeLengths = new double[curvePoints.length];
        double length = countLength(curvePoints, cumulativeLengths);
        double step = length / m;
        int gridI = 1;
        gridPoints[0] = new Point(curvePoints[0].getX(), curvePoints[0].getY());
        for (int i = 1; i < curvePoints.length; ++i) {
            double left = cumulativeLengths[i - 1];
            double right = cumulativeLengths[i];
            while (gridI * step > left && gridI * step <= right) {
                gridPoints[gridI] = lengthInterpolate(curvePoints[i - 1], curvePoints[i],
                        right - left, gridI * step - left);
                ++gridI;
            }
        }
        return gridPoints;
    }

    private static Point[] buildCurve(Point[] generatrixPoints, int tN, double[][] tParams) {
        if (generatrixPoints.length < Curve.minPointsNum || generatrixPoints.length > Curve.maxPointsNum) {
            throw new IllegalArgumentException();
        }
        Point[] curvePoints = new Point[(generatrixPoints.length - 3) * tN + 1];
        for (int i = 0; i < generatrixPoints.length - 3; ++i) {
            for (int j = 0; j < tN + 1; ++j) {
                if (j == tN && i != generatrixPoints.length - 4) {
                    continue;
                }
                double x = generatrixPoints[i].getX() * tParams[j][0] + generatrixPoints[i + 1].getX() * tParams[j][1] +
                        generatrixPoints[i + 2].getX() * tParams[j][2] + generatrixPoints[i + 3].getX() * tParams[j][3];
                double y = generatrixPoints[i].getY() * tParams[j][0] + generatrixPoints[i + 1].getY() * tParams[j][1] +
                        generatrixPoints[i + 2].getY() * tParams[j][2] + generatrixPoints[i + 3].getY() * tParams[j][3];
                curvePoints[i * tN + j] = new Point((float)x, (float)y);
            }
        }
        return curvePoints;
    }

    private static void drawCurves(WritableImage img, Vector3D[][] curveWireframeVertices, Color color) {
        for (Vector3D[] curveVertices : curveWireframeVertices) {
            for (int j = 0; j < curveVertices.length - 1; ++j) {
                Vector3D p1 = curveVertices[j];
                Vector3D p2 = curveVertices[j + 1];
                Graphics2D.drawLine(img, p1.getX(), p1.getY(), p2.getX(), p2.getY(), color, 1);
            }
        }
    }

    private static void drawMGrid(WritableImage img, Vector3D[][] mGridWireframeVertices, Color color) {
        for (int j = 0; j < mGridWireframeVertices[0].length; ++j) {
            for (int i = 0; i < mGridWireframeVertices.length - 1; ++i) {
                Vector3D p1 = mGridWireframeVertices[i][j];
                Vector3D p2 = mGridWireframeVertices[i + 1][j];
                Graphics2D.drawLine(img, p1.getX(), p1.getY(), p2.getX(), p2.getY(), color, 1);
            }
            Vector3D p1 = mGridWireframeVertices[mGridWireframeVertices.length - 1][j];
            Vector3D p2 = mGridWireframeVertices[0][j];
            Graphics2D.drawLine(img, p1.getX(), p1.getY(), p2.getX(), p2.getY(), color, 1);
        }
    }

    public static void drawWireframeObject(WritableImage img, Point[] generatrixPoints,
                                           RealMatrix modelMatrix, RealMatrix viewMatrix, RealMatrix projectionMatrix,
                                           int m, int n, int k, int tN, Color color) {
        double[][] tParams = getTParams(tN);
        Point[] curvePoints = buildCurve(generatrixPoints, tN, tParams);
        Point[] gridPoints = getMGridPoints(curvePoints, m);
        Vector3D[][] curveWireframeVertices = getRotatedVertices(curvePoints, n, 0);
        Vector3D[][] gridWireframeVertices = getRotatedVertices(gridPoints, n, k);
        vertexShader(curveWireframeVertices, modelMatrix, viewMatrix, projectionMatrix, img.getWidth(), img.getHeight());
        vertexShader(gridWireframeVertices, modelMatrix, viewMatrix, projectionMatrix, img.getWidth(), img.getHeight());
        drawCurves(img, curveWireframeVertices, color);
        drawMGrid(img, gridWireframeVertices, color);
    }
}

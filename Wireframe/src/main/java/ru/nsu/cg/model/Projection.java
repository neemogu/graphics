package ru.nsu.cg.model;

import lombok.Getter;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class Projection {
    @Getter
    private double near = 0.5;
    private static final double minNear = 0.1;
    private static final double maxNear = 5.0;
    @Getter
    private final double far = 10.0;
    private static final boolean zoomFov = false;
    @Getter
    private double fov = (float)(Math.PI / 2);
    private static final double minFov = (float)(Math.PI / 12);
    private static final double maxFov = (float)(Math.PI);

    public RealMatrix getProjectionMatrix(double screenWidth, double screenHeight) {
        double tanHalfFov = Math.tan(fov / 2);
        double aspect = screenWidth / screenHeight;
        return new Array2DRowRealMatrix(new double[][]{
                {near / (aspect * tanHalfFov), 0, 0, 0},
                {0, near / tanHalfFov, 0, 0},
                {0, 0, -(far + near) / (far - near), -(2 * far * near) / (far - near)},
                {0, 0, -1, 0}
        });
    }

    public void zoomIn() {
        if (zoomFov) {
            fov -= 0.05f;
            if (fov < minFov) {
                fov = minFov;
            }
        } else {
            near += 0.1;
            if (near > maxNear) {
                near = maxNear;
            }
        }

    }

    public void zoomOut() {
        if (zoomFov) {
            fov += 0.05f;
            if (fov > maxFov) {
                fov = maxFov;
            }
        } else {
            near -= 0.1;
            if (near < minNear) {
                near = minNear;
            }
        }
    }
}

package ru.nsu.cg.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;


public class Camera {
    private RealMatrix viewMatrix;
    private boolean recalculateViewMatrix = false;
    private Vector3D upVec;
    private Vector3D location;
    private Vector3D viewVec;

    public Camera(Vector3D location, Vector3D viewVec, Vector3D upVec) {
        setLocation(location);
        setViewVec(viewVec);
        setUpVec(upVec);
    }

    public RealMatrix getViewMatrix() {
        if (recalculateViewMatrix) {
            calculateViewMatrix();
        }
        return viewMatrix;
    }

    public void setUpVec(Vector3D upVec) {
        this.upVec = upVec;
        recalculateViewMatrix = true;
    }

    public void setViewVec(Vector3D viewVec) {
        this.viewVec = viewVec.normalize();
        recalculateViewMatrix = true;
    }

    public void setLocation(Vector3D location) {
        this.location = location;
        recalculateViewMatrix = true;
    }

    private void calculateViewMatrix() {
        Vector3D rightVec = Vector3D.crossProduct(this.upVec, viewVec).normalize();
        Vector3D upVec = Vector3D.crossProduct(viewVec, rightVec).normalize();
        double[][] matrixData = new double[][] {
                {rightVec.getX(), rightVec.getY(), rightVec.getZ(), 0},
                {upVec.getX(), upVec.getY(), upVec.getZ(), 0},
                {viewVec.getX(), viewVec.getY(), viewVec.getZ(), 0},
                {0, 0, 0, 1}
        };
        RealMatrix rotationMatrix = new Array2DRowRealMatrix(matrixData);
        matrixData = new double[][] {
                {1, 0, 0, -location.getX()},
                {0, 1, 0, -location.getY()},
                {0, 0, 1, -location.getZ()},
                {0, 0, 0, 1}
        };
        RealMatrix translationMatrix = new Array2DRowRealMatrix(matrixData);
        viewMatrix = rotationMatrix.multiply(translationMatrix);
    }
}

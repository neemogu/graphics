package ru.nsu.cg.model;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import ru.nsu.cg.Subscriber;

import java.util.ArrayList;

public class WireframeModel {
    public static final int startWidth = 640;
    public static final int startHeight = 480;

    private WritableImage screen = new WritableImage(startWidth, startHeight);

    private final ArrayList<Subscriber> subs = new ArrayList<>();

    private final Camera camera = new Camera(
            new Vector3D(0.0, 0.0, 1.0),
            new Vector3D(0.0, 0.0, -1.0),
            new Vector3D(0.0, 1.0, 0.0)
    );
    @Getter
    private final Projection projection = new Projection();
    @Getter
    private final SettingsModel settingsModel = new SettingsModel();

    @Getter
    private float yaw = 90.0f;
    @Getter
    private float roll = 0.0f;

    public WireframeModel() {
        updateScreen();
    }

    public int getScreenWidth() {
        return (int)screen.getWidth();
    }

    public int getScreenHeight() {
        return (int)screen.getHeight();
    }

    public void resize(int newWidth, int newHeight) {
        screen = new WritableImage(newWidth, newHeight);
        updateScreen();
    }

    public void updateScreen() {
        Graphics2D.clear(screen, Color.WHITE);
        Point[] curvePoints = settingsModel.getCurve().getNDCPoints();
        Graphics3D.drawWireframeObject(
                screen,
                curvePoints,
                ModelTransforms.yaw(ModelTransforms.roll(ModelTransforms.getStartMatrix(), roll), yaw),
                camera.getViewMatrix(),
                projection.getProjectionMatrix(screen.getWidth(), screen.getHeight()),
                settingsModel.getCurveSettings().getM(),
                settingsModel.getCurveSettings().getN(),
                settingsModel.getCurveSettings().getK(),
                settingsModel.getCurveSettings().getTN(),
                Color.color(
                        settingsModel.getCurveSettings().getR() / 255.0,
                        settingsModel.getCurveSettings().getG() / 255.0,
                        settingsModel.getCurveSettings().getB() / 255.0
                )
        );
        notifySubscribers();
    }

    public void resetRotations() {
        yaw = 90.0f;
        roll = 0.0f;
        updateScreen();
    }

    public Image getScreen() {
        return screen;
    }

    public void addYaw(float yaw) {
        this.yaw += yaw;
        if (this.yaw >= 360) {
            this.yaw = this.yaw - 360;
        }
        if (this.yaw < 0) {
            this.yaw = 360 + this.yaw;
        }
    }

    public void addRoll(float roll) {
        this.roll += roll;
        if (this.roll >= 360) {
            this.roll = this.roll - 360;
        }
        if (this.roll < 0) {
            this.roll = 360 + this.roll;
        }
    }

    public void addSubscriber(Subscriber sub) {
        subs.add(sub);
    }

    private void notifySubscribers() {
        for (Subscriber sub : subs) {
            sub.update();
        }
    }
}

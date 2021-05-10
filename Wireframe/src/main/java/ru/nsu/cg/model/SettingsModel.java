package ru.nsu.cg.model;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.Getter;
import ru.nsu.cg.Subscriber;

import java.util.ArrayList;

public class SettingsModel {
    public static final int startWidth = 640;
    public static final int startHeight = 480;
    @Getter
    private final Curve curve = new Curve();
    @Getter
    private final CurveSettings curveSettings = new CurveSettings();
    private WritableImage curveImage = new WritableImage(startWidth, startHeight);

    private final ArrayList<Subscriber> subs = new ArrayList<>();

    public SettingsModel() {
        updateCurveImage();
    }

    public int getCurveImageWidth() {
        return (int)curveImage.getWidth();
    }

    public int getCurveImageHeight() {
        return (int)curveImage.getHeight();
    }

    public void resizeCurveImage(int newWidth, int newHeight) {
        curveImage = new WritableImage(newWidth, newHeight);
        updateCurveImage();
    }

    public void updateCurveImage() {
        Graphics2D.clear(curveImage, Color.color(0.1, 0.1, 0.1));
        Graphics2D.drawAxes(curveImage);
        Point[] curvePoints = curve.getPoints();
        Graphics2D.drawCurve(curveImage, curvePoints, curveSettings.getTN(), Color.WHITE);
        Graphics2D.drawCircles(
                curveImage,
                curvePoints,
                curve.getSelectedPoint(),
                Math.max((int)(Curve.pointRadius * Math.min(curveImage.getWidth(), curveImage.getHeight())), 1),
                Color.MAGENTA,
                Color.CYAN);
        notifySubscribers();
    }

    public Image getCurveImage() {
        return curveImage;
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

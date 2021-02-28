package ru.nsu.cg;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class PaintSettings {
    public static final int maxThickness = 50;
    private int lineThickness;
    private int lineThicknessSaved;
    public static final int maxStampVertexNum = 16;
    public static final int minStampVertexNum = 3;
    private boolean isStarStamp;
    private boolean isStarStampSaved;
    private int stampVertexNum;
    private int stampVertexNumSaved;
    public static final int maxStampSize = 600;
    public static final int minStampSize = 50;
    private int stampSize;
    private int stampSizeSaved;
    private int stampRotation;
    private int stampRotationSaved;
    private PaintMode mode;
    private Color color;
    private final ArrayList<Subscriber> subs = new ArrayList<>();

    public PaintSettings() {
        lineThickness = 1;
        stampVertexNum = 5;
        stampSize = 50;
        stampRotation = 0;
        isStarStamp = false;
        mode = PaintMode.LINE;
        color = Color.BLACK;
        saveValues();
    }

    public void saveValues() {
        lineThicknessSaved = lineThickness;
        stampVertexNumSaved = stampVertexNum;
        stampSizeSaved = stampSize;
        stampRotationSaved = stampRotation;
        isStarStampSaved = isStarStamp;
    }

    public void recoverSavedValues() {
        lineThickness = lineThicknessSaved;
        stampVertexNum = stampVertexNumSaved;
        stampSize = stampSizeSaved;
        stampRotation = stampRotationSaved;
        isStarStamp = isStarStampSaved;
        notifySubscribers();
    }

    public void addSubscriber(Subscriber sub) {
        subs.add(sub);
    }

    public void notifySubscribers() {
        for (Subscriber sub : subs) {
            sub.update();
        }
    }

    public int getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(int lineThickness) {
        if (lineThickness <= 0) {
            this.lineThickness = 1;
        } else {
            this.lineThickness = Math.min(lineThickness, maxThickness);
        }
        notifySubscribers();
    }

    public boolean isStarStamp() {
        return isStarStamp;
    }

    public void setStarStamp(boolean starStamp) {
        isStarStamp = starStamp;
        notifySubscribers();
    }

    public int getStampVertexNum() {
        return stampVertexNum;
    }

    public void setStampVertexNum(int stampVertexNum) {
        if (stampVertexNum < minStampVertexNum) {
            this.stampVertexNum = minStampVertexNum;
        } else {
            this.stampVertexNum = Math.min(stampVertexNum, maxStampVertexNum);
        }
        notifySubscribers();
    }

    public int getStampSize() {
        return stampSize;
    }

    public void setStampSize(int stampSize) {
        if (stampSize < minStampSize) {
            this.stampSize = minStampSize;
        } else {
            this.stampSize = Math.min(stampSize, maxStampSize);
        }
        notifySubscribers();
    }

    public int getStampRotation() {
        return stampRotation;
    }

    public void setStampRotation(int stampRotation) {
        this.stampRotation = (((stampRotation % 360) + 360) % 360);
        notifySubscribers();
    }

    public PaintMode getMode() {
        return mode;
    }

    public void setMode(PaintMode mode) {
        this.mode = mode;
        notifySubscribers();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        notifySubscribers();
    }
}

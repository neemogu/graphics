package ru.nsu.cg.model;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import ru.nsu.cg.Subscriber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MapSettings {
    public static final int minIsolinesCount = 1;
    public static final int maxIsolinesCount = 100;
    private final ArrayList<Color> colors = new ArrayList<>();
    private final ArrayList<Color> savedColors = new ArrayList<>();
    @Getter
    @Setter
    private Color isolineColor = Color.BLACK;
    private Color savedIsolineColor = isolineColor;
    @Getter
    private int isolinesCount = minIsolinesCount;
    private int savedIsolinesCount = isolinesCount;
    @Getter
    private Interpolation.InterpolationType interpolationType;
    private Interpolation.InterpolationType savedInterpolationType = Interpolation.InterpolationType.BILINEAR;
    @Getter
    @Setter
    private boolean gridVisible = false;
    @Getter
    @Setter
    private boolean dynamicIsolineVisible = false;
    @Getter
    @Setter
    private boolean isolinesVisible = false;
    @Getter
    @Setter
    private DisplayMode displayMode = DisplayMode.SMOOTH;
    private final ArrayList<Subscriber> subs = new ArrayList<>();

    public MapSettings() {
        colors.add(Color.MAGENTA);
        colors.add(Color.MAROON);
    }

    public enum DisplayMode { SMOOTH, COLOR_MAP }

    public Color[] getColors() {
        Color[] result = new Color[colors.size()];
        return colors.toArray(result);
    }

    public void clearColors() {
        colors.clear();
    }

    public void addColor(Color color) {
        colors.add(color);
    }

    public void setIsolinesCount(int isolinesCount) throws IllegalArgumentException {
        if (isolinesCount >= minIsolinesCount && isolinesCount <= maxIsolinesCount) {
            this.isolinesCount = isolinesCount;
            int colorsCount = isolinesCount + 1;
            if (colorsCount < colors.size()) {
                colors.subList(colorsCount, colors.size()).clear();
            } else if (colorsCount > colors.size()) {
                for (int i = colors.size(); i < colorsCount; ++i) {
                    colors.add(Color.BLACK);
                }
            }
            notifySubscribers();
        } else {
            throw new IllegalArgumentException("Isolines number must be between " + minIsolinesCount + " and " + maxIsolinesCount);
        }
    }

    public void saveSettings() {
        savedColors.clear();
        savedColors.addAll(colors);
        savedIsolineColor = isolineColor;
        savedIsolinesCount = isolinesCount;
        savedInterpolationType = interpolationType;
    }

    public void recoverSettings() {
        colors.clear();
        colors.addAll(savedColors);
        isolinesCount = savedIsolinesCount;
        isolineColor = savedIsolineColor;
        interpolationType = savedInterpolationType;
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

package ru.nsu.cg.model;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.nsu.cg.Subscriber;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MapModel {
    public static final int startWidth = 640;
    public static final int startHeight = 480;

    @Getter
    private final MapSettings settings = new MapSettings();
    @Getter
    private final Function function = new Function();
    private double[][] functionGrid = function.getCalculatedMatrix();

    private WritableImage mainImage;
    private WritableImage isolines;
    private WritableImage dynamicIsoline;
    private WritableImage grid;
    private WritableImage composedImage;

    private WritableImage legend;

    private boolean isComposed = false;

    private final ArrayList<Subscriber> subs = new ArrayList<>();

    private int getLegendWidth() {
        return (int)(mainImage.getWidth() * 9 / 10);
    }

    private int getLegendHeight() {
        return (int)(mainImage.getHeight() / 10);
    }

    public MapModel() {
        resize(startWidth, startHeight);
        update();
    }

    public double[] getLegendLabels() {
        double[] labels = new double[settings.getDisplayMode() == MapSettings.DisplayMode.SMOOTH ?
                settings.getIsolinesCount() + 1 : settings.getIsolinesCount() + 2];
        double min = Function.gridMin(functionGrid);
        double max = Function.gridMax(functionGrid);
        double delta = (max - min) / (labels.length - 1);
        for (int i = 0; i < labels.length; ++i) {
            labels[i] = min + i * delta;
        }
        return labels;
    }

    public Image getLegendImage() {
        return legend;
    }

    public boolean saveImage(File imageFile) {
        if (imageFile == null) {
            return true;
        }
        BufferedImage img = SwingFXUtils.fromFXImage(getImage(), null);
        try {
            ImageIO.write(img, "png", imageFile);
        } catch (IOException e) {
            System.err.println("Unable to save " + imageFile.getName());
            return false;
        }
        return true;
    }

    public void updateFunctionMatrix() {
        updateFunctionMatrixWithoutNotifying();
        notifySubscribers();
    }

    private void updateFunctionMatrixWithoutNotifying() {
        functionGrid = function.getCalculatedMatrix();
        isComposed = false;
    }

    private void clearImage(WritableImage img) {
        for (int y = 0; y < img.getHeight(); ++y) {
            for (int x = 0; x < img.getWidth(); ++x) {
                img.getPixelWriter().setColor(x, y, Color.TRANSPARENT);
            }
        }
    }

    public void updateGrid() {
        updateGridWithoutNotifying();
        notifySubscribers();
    }

    private void updateGridWithoutNotifying() {
        clearImage(grid);
        Graphics.drawGrid(grid, functionGrid[0].length, functionGrid.length, 1, Color.BLACK);
        isComposed = false;
    }

    public void setDynamicIsoline(double value) {
        clearImage(dynamicIsoline);
        Graphics.drawIsoline(dynamicIsoline, settings.getIsolineColor(), 1, value, functionGrid);
        isComposed = false;
    }

    public void updateIsolines() {
        updateIsolinesWithoutNotifying();
        notifySubscribers();
    }

    private void updateIsolinesWithoutNotifying() {
        clearImage(isolines);
        Graphics.drawIsolines(isolines, settings.getIsolinesCount(), settings.getIsolineColor(), functionGrid, 1);
        isComposed = false;
    }

    public double getFunctionX(double imageX) {
        return function.getStartX() + (function.getEndX() - function.getStartX()) / mainImage.getWidth() * imageX;
    }

    public double getFunctionY(double imageY) {
        return function.getStartY() + (function.getEndY() - function.getStartY()) / mainImage.getHeight() * (mainImage.getHeight() - 1 - imageY);
    }

    public double getFunctionValue(double imageX, double imageY) {
        return function.calculate(getFunctionX(imageX), getFunctionY(imageY));
    }


    public void updateImage() {
        updateImageWithoutNotifying();
        notifySubscribers();
    }

    private void updateImageWithoutNotifying() {
        clearImage(mainImage);
        clearImage(legend);
        Color[] colors = settings.getColors();
        switch (settings.getDisplayMode()) {
            case SMOOTH:
                Graphics.drawSmoothMap(mainImage, functionGrid, colors);
                Graphics.drawSmoothLegend(legend, colors);
                break;
            case COLOR_MAP:
                Graphics.drawColorMap(mainImage, functionGrid, colors);
                Graphics.drawColorMapLegend(legend, colors);
                break;
        }
        isComposed = false;
    }

    public Image getImage() {
        if (isComposed) {
            return composedImage;
        }
        composedImage.getPixelWriter().setPixels(0, 0, (int)composedImage.getWidth(), (int)composedImage.getHeight(),
                mainImage.getPixelReader(), 0, 0);
        if (settings.isGridVisible()) {
            Graphics.blend(composedImage, grid, composedImage);
        }
        if (settings.isIsolinesVisible()) {
            Graphics.blend(composedImage, isolines, composedImage);
        }
        if (settings.isDynamicIsolineVisible()) {
            Graphics.blend(composedImage, dynamicIsoline, composedImage);
        }
        isComposed = true;
        return composedImage;
    }

    public void addSubscriber(Subscriber sub) {
        subs.add(sub);
    }

    private void notifySubscribers() {
        for (Subscriber sub : subs) {
            sub.update();
        }
    }

    public void resize(int width, int height) {
        mainImage = new WritableImage(width, height);
        dynamicIsoline = new WritableImage(width, height);
        isolines = new WritableImage(width, height);
        grid = new WritableImage(width, height);
        composedImage = new WritableImage(width, height);

        legend = new WritableImage(getLegendWidth(), getLegendHeight());

        updateGridWithoutNotifying();
        updateImageWithoutNotifying();
        updateIsolinesWithoutNotifying();
        notifySubscribers();
    }

    public void update() {
        updateFunctionMatrixWithoutNotifying();
        updateGridWithoutNotifying();
        updateIsolinesWithoutNotifying();
        updateImageWithoutNotifying();
        notifySubscribers();
    }
}

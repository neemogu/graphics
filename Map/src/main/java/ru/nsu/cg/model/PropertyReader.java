package ru.nsu.cg.model;

import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.*;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class PropertyReader {
    private final MapModel model;

    private int getIntProperty(Properties properties, String propertyName, int defaultValue)
            throws PropertyException {
        String strProperty = properties.getProperty(propertyName);
        if (strProperty == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(strProperty);
        } catch (NumberFormatException e) {
            throw new PropertyException("Invalid '" + propertyName + "' property format");
        }
    }

    private int getRequiredIntProperty(Properties properties, String propertyName) throws PropertyException {
        String strProperty = properties.getProperty(propertyName);
        if (strProperty == null) {
            throw new PropertyException("Missing '" + propertyName + "' property");
        }
        try {
            return Integer.parseInt(strProperty);
        } catch (NumberFormatException e) {
            throw new PropertyException("Invalid '" + propertyName + "' property format");
        }
    }

    private double getDoubleProperty(Properties properties, String propertyName, double defaultValue)
            throws PropertyException {
        String strProperty = properties.getProperty(propertyName);
        if (strProperty == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(strProperty);
        } catch (NumberFormatException e) {
            throw new PropertyException("Invalid '" + propertyName + "' property format");
        }
    }

    private <T> void setBiProperty(T firstProperty, T secondProperty, BiConsumer<T, T> setter)
            throws PropertyException {
        try {
            setter.accept(firstProperty, secondProperty);
        } catch (IllegalArgumentException e) {
            throw new PropertyException(e.getMessage());
        }
    }

    private <T> void setProperty(T property, Consumer<T> setter) throws PropertyException {
        try {
            setter.accept(property);
        } catch (IllegalArgumentException e) {
            throw new PropertyException(e.getMessage());
        }
    }

    private void setColorProperty(int red, int green, int blue, Consumer<Color> setter) throws PropertyException {
        try {
            setter.accept(Color.rgb(red, green, blue));
        } catch (IllegalArgumentException e) {
            throw new PropertyException("Color component must be between 0 and 255");
        }
    }

    public void setProperties(Properties properties) throws PropertyException {
        int gridWidth = getIntProperty(properties, "gridWidth", model.getFunction().getGridWidth());
        int gridHeight = getIntProperty(properties, "gridHeight", model.getFunction().getGridHeight());
        setBiProperty(gridWidth, gridHeight, model.getFunction()::setGridSize);
        double startX = getDoubleProperty(properties, "startX", model.getFunction().getStartX());
        double endX = getDoubleProperty(properties, "endX", model.getFunction().getEndX());
        setBiProperty(startX, endX, model.getFunction()::setBoundsX);
        double startY = getDoubleProperty(properties, "startY", model.getFunction().getStartY());
        double endY = getDoubleProperty(properties, "endY", model.getFunction().getEndY());
        setBiProperty(startY, endY, model.getFunction()::setBoundsY);
        int isolinesCount = getIntProperty(properties, "isolinesCount", model.getSettings().getIsolinesCount());
        setProperty(isolinesCount, model.getSettings()::setIsolinesCount);
        int Rz = getIntProperty(properties, "Rz", (int) Math.round(model.getSettings().getIsolineColor().getRed() * 255));
        int Gz = getIntProperty(properties, "Gz", (int) Math.round(model.getSettings().getIsolineColor().getGreen() * 255));
        int Bz = getIntProperty(properties, "Bz", (int) Math.round(model.getSettings().getIsolineColor().getBlue() * 255));
        setColorProperty(Rz, Gz, Bz, model.getSettings()::setIsolineColor);
        model.getSettings().clearColors();
        for (int i = 0; i < isolinesCount + 1; ++i) {
            int R = getRequiredIntProperty(properties, "R" + i);
            int G = getRequiredIntProperty(properties, "G" + i);
            int B = getRequiredIntProperty(properties, "B" + i);
            setColorProperty(R, G, B, model.getSettings()::addColor);
        }
        model.update();
    }

    public void readPropertiesFromFile(File file) throws PropertyException {
        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new PropertyException("File not found");
        }
        Properties properties;
        try {
            properties = new Properties();
            properties.load(is);
        } catch (IOException e) {
            throw new PropertyException("File reading error");
        }
        model.getSettings().saveSettings();
        model.getFunction().saveParameters();
        try {
            setProperties(properties);
        } catch (PropertyException e) {
            model.getFunction().recoverParameters();
            model.getSettings().recoverSettings();
            throw e;
        }
    }

    public static class PropertyException extends Exception {
        public PropertyException(String message) {
            super(message);
        }
    }
}

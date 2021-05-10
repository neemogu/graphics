package ru.nsu.cg.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ModelFile {
    public static void saveModel(File file, WireframeModel model) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("m", "" + model.getSettingsModel().getCurveSettings().getM());
        properties.setProperty("n", "" + model.getSettingsModel().getCurveSettings().getN());
        properties.setProperty("k", "" + model.getSettingsModel().getCurveSettings().getK());
        properties.setProperty("tN", "" + model.getSettingsModel().getCurveSettings().getTN());
        properties.setProperty("R", "" + model.getSettingsModel().getCurveSettings().getR());
        properties.setProperty("G", "" + model.getSettingsModel().getCurveSettings().getG());
        properties.setProperty("B", "" + model.getSettingsModel().getCurveSettings().getB());
        Point[] points = model.getSettingsModel().getCurve().getPoints();
        properties.setProperty("pointsNum", "" + points.length);
        for (int i = 0; i < points.length; ++i) {
            properties.setProperty("p" + i, "" + points[i].getX() + "," + points[i].getY());
        }
        try {
            OutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
            properties.store(stream, null);
        } catch (FileNotFoundException e) {
            throw new IOException("File " + file.getName() + " not found");
        } catch (IOException e) {
            throw new IOException("Error saving model to " + file.getName());
        }
    }

    public static void openModel(File modelFile, WireframeModel model) throws IOException {
        Properties properties = new Properties();
        try {
            InputStream stream = new BufferedInputStream(new FileInputStream(modelFile));
            properties.load(stream);
        } catch (IOException e) {
            throw new IOException("Error reading model from " + modelFile.getName() + " file");
        }
        getModelProperty(model.getSettingsModel().getCurveSettings()::setM, properties, "m");
        getModelProperty(model.getSettingsModel().getCurveSettings()::setN, properties, "n");
        getModelProperty(model.getSettingsModel().getCurveSettings()::setK, properties, "k");
        getModelProperty(model.getSettingsModel().getCurveSettings()::settN, properties, "tN");
        getModelProperty(model.getSettingsModel().getCurveSettings()::setR, properties, "R");
        getModelProperty(model.getSettingsModel().getCurveSettings()::setG, properties, "G");
        getModelProperty(model.getSettingsModel().getCurveSettings()::setB, properties, "B");
        AtomicInteger pointsNumProperty = new AtomicInteger();
        getModelProperty(pointsNumProperty::set, properties, "pointsNum");
        int pointsNum = pointsNumProperty.intValue();
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0; i < pointsNum; ++i) {
            Point p = new Point(0, 0);
            getPoint(p, properties, i);
            points.add(p);
        }
        try {
            model.getSettingsModel().getCurve().setPoints(points);
            model.getSettingsModel().getCurve().setSelectedPoint(points.get(0).getX(), points.get(0).getY());
        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    private static void getModelProperty(Consumer<Integer> setter,
                                  Properties properties, String propertyName) throws IOException {
        try {
            setter.accept(Integer.parseInt(properties.getProperty(propertyName)));
        } catch (NullPointerException e) {
            throw new IOException(propertyName + " property hasn't been found");
        } catch (NumberFormatException e) {
            throw new IOException(propertyName + " property has invalid format");
        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    private static void getPoint(Point p, Properties properties, int i) throws IOException {
        try {
            String[] pointStrs = properties.getProperty("p" + i).split(",");
            p.setX(Float.parseFloat(pointStrs[0]));
            p.setY(Float.parseFloat(pointStrs[1]));
        } catch (NullPointerException e) {
            throw new IOException("Point № " + i + " property hasn't been found");
        } catch (NumberFormatException e) {
            throw new IOException("Point № " + i + " property has invalid format");
        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Point № " + i + " property doesn't have x or/and y values");
        }
    }
}

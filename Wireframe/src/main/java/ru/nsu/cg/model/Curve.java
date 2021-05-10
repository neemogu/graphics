package ru.nsu.cg.model;

import lombok.*;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import ru.nsu.cg.Subscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Curve {
    public static final int minPointsNum = 5;
    public static final int maxPointsNum = 50;
    public static final float pointRadius = 0.02f;
    @Getter
    private int selectedPoint = 0;

    private ArrayList<Point> points = new ArrayList<>();

    private final ArrayList<Subscriber> subs = new ArrayList<>();

    private ArrayList<Point> savedPoints;

    public int getPointsNum() {
        return points.size();
    }

    public void save() {
        savedPoints = new ArrayList<>();
        for (Point p : points) {
            savedPoints.add(new Point(p.getX(), p.getY()));
        }
    }

    public void addPoint() {
        if (points.size() < maxPointsNum) {
            points.add(new Point(0.5f, 0.5f));
            selectedPoint = points.size() - 1;
            notifySubscribers();
        }
    }

    public void removePoint() {
        if (points.size() > minPointsNum) {
            points.remove(points.size() - 1);
            selectedPoint = points.size() - 1;
            notifySubscribers();
        }
    }

    public void setSelectedPoint(float x, float y) {
        for (int i = 0; i < points.size(); ++i) {
            Point p = points.get(i);
            if (p.getX() >= x - pointRadius && p.getX() <= x + pointRadius &&
                    p.getY() >= y - pointRadius && p.getY() <= y + pointRadius) {
                selectedPoint = i;
                notifySubscribers();
                break;
            }
        }
    }

    public void setPoints(ArrayList<Point> points) {
        if (points.size() < minPointsNum || points.size() > maxPointsNum) {
            throw new IllegalArgumentException("Points number must be between " + minPointsNum + " and " + maxPointsNum);
        }
        for (Point p : points) {
            p.setX(clamp(p.getX()));
            p.setY(clamp(p.getY()));
        }
        this.points = points;
        this.savedPoints = points;
    }

    public void recover() {
        points = savedPoints;
        selectedPoint = 0;
    }

    public Curve() {
        points.add(new Point(0.1f, 0.4f));
        points.add(new Point(0.3f, 0.1f));
        points.add(new Point(0.4f, 0.8f));
        points.add(new Point(0.6f, 0.4f));
        points.add(new Point(0.7f, 0.7f));
        points.add(new Point(0.9f, 0.3f));
        savedPoints = points;
    }

    private float clamp(float value) {
        if (value < 0.0f) {
            return 0.0f;
        }
        return Math.min(value, 1.0f);
    }

    public void movePoint(float x, float y) {
        points.get(selectedPoint).setX(clamp(x));
        points.get(selectedPoint).setY(clamp(y));
        notifySubscribers();
    }

    public Point[] getPoints() {
        Point[] result = new Point[getPointsNum()];
        for (int i = 0; i < getPointsNum(); ++i) {
            Point p = points.get(i);
            result[i] = new Point(p.getX(), p.getY());
        }
        return result;
    }

    public Point[] getNDCPoints() {
        Point[] result = new Point[getPointsNum()];
        for (int i = 0; i < getPointsNum(); ++i) {
            Point p = points.get(i);
            result[i] = new Point((p.getX() - 0.5f) * 2, (1.0f - p.getY() - 0.5f) * 2);
        }
        return result;
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

package ru.nsu.cg.model;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Stack;

public class Graphics2D {
    public static void clear(WritableImage img, Color color) {
        for (int i = 0; i < img.getHeight(); ++i) {
            for (int j = 0; j < img.getWidth(); ++j) {
                img.getPixelWriter().setColor(j, i, color);
            }
        }
    }

    public static void drawAxes(WritableImage img) {
        for (int i = 0; i < img.getHeight(); ++i) {
            img.getPixelWriter().setColor((int)(img.getWidth() / 2), i, Color.DARKRED);
        }
        for (int i = 0; i < img.getWidth(); ++i) {
            img.getPixelWriter().setColor(i, (int)(img.getHeight() / 2), Color.DARKRED);
        }
    }

    private static void drawCircle(WritableImage img, int xc, int yc, int x, int y, Color color)
    {
        int imgWidth = (int)img.getWidth();
        int imgHeight = (int)img.getHeight();
        setPixel(img, imgWidth, imgHeight, xc + x, yc + y, color);
        setPixel(img, imgWidth, imgHeight, xc - x, yc + y, color);
        setPixel(img, imgWidth, imgHeight, xc + x, yc - y, color);
        setPixel(img, imgWidth, imgHeight, xc - x, yc - y, color);
        setPixel(img, imgWidth, imgHeight, xc - y, yc + x, color);
        setPixel(img, imgWidth, imgHeight, xc + y, yc - x, color);
        setPixel(img, imgWidth, imgHeight, xc + y, yc + x, color);
        setPixel(img, imgWidth, imgHeight, xc - y, yc - x, color);
    }

    private static void circleBresenham(WritableImage img, int xc, int yc, int r, Color color)
    {
        int x = 0, y = r;
        int d = 3 - 2 * r;
        drawCircle(img, xc, yc, x, y, color);
        while (y >= x)
        {
            x++;
            if (d > 0) {
                --y;
                d = d + 4 * (x - y) + 10;
            }
            else {
                d = d + 4 * x + 6;
            }
            drawCircle(img, xc, yc, x, y, color);
        }
    }

    public static void drawCircles(WritableImage img, Point[] points, int selectedPoint, int radius,
                                   Color color, Color selectedColor) {
        if (selectedPoint < 0 || selectedPoint >= points.length) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < points.length; ++i) {
            Point p = points[i];
            if (i != selectedPoint) {
                circleBresenham(img, (int) (p.getX() * img.getWidth()), (int) (p.getY() * img.getHeight()), radius, color);
            } else {
                circleBresenham(img, (int) (p.getX() * img.getWidth()), (int) (p.getY() * img.getHeight()), radius, selectedColor);
            }
        }
    }

    public static double[][] getTParams(int tN) {
        double[][] result = new double[tN + 1][4];
        double[] tParams = new double[tN + 1];
        for (int i = 0; i < tN + 1; ++i) {
            tParams[i] = i * (1.0 / tN);
        }
        for (int j = 0; j < tN + 1; ++j) {
            result[j][0] = (-1.0 / 6) * Math.pow(tParams[j], 3) + 0.5 * Math.pow(tParams[j], 2) -
                    0.5 * tParams[j] + 1.0 / 6;
            result[j][1] = 0.5 * Math.pow(tParams[j], 3) - Math.pow(tParams[j], 2) + 4.0 / 6;
            result[j][2] = (-0.5) * Math.pow(tParams[j], 3) + 0.5 * Math.pow(tParams[j], 2) +
                    0.5 * tParams[j] + 1.0 / 6;
            result[j][3] = (1.0 / 6) * Math.pow(tParams[j], 3);
        }
        return result;
    }

    public static void drawCurve(WritableImage img, Point[] points, int tN, Color color) {
        if (points.length < Curve.minPointsNum) {
            throw new IllegalArgumentException();
        }
        double[][] tParams = getTParams(tN);
        for (int i = 0; i < points.length - 3; ++i) {
            double prevX = 0;
            double prevY = 0;
            for (int j = 0; j < tN + 1; ++j) {
                double x = points[i].getX() * tParams[j][0] + points[i + 1].getX() * tParams[j][1] +
                        points[i + 2].getX() * tParams[j][2] + points[i + 3].getX() * tParams[j][3];
                double y = points[i].getY() * tParams[j][0] + points[i + 1].getY() * tParams[j][1] +
                        points[i + 2].getY() * tParams[j][2] + points[i + 3].getY() * tParams[j][3];
                if (j != 0) {
                    drawLine(img, prevX * (img.getWidth() - 1), prevY * (img.getHeight() - 1),
                            x * (img.getWidth() - 1), y * (img.getHeight() - 1), color, 1);
                }
                prevX = x;
                prevY = y;
            }
        }
    }

    private static void drawThinLine(WritableImage img, double x1, double y1, double x2, double y2, Color color,
                                     int[] leftBuffer, int[] rightBuffer) {
        final int x0 = (int)Math.round(x1);
        final int y0 = (int)Math.round(y1);
        final int x = (int)Math.round(x2);
        final int y = (int)Math.round(y2);
        int dx = Math.abs(x - x0);
        int dy = Math.abs(y - y0);
        int sx = dx != 0 ? (x - x0) / dx : 0;
        int sy = dy != 0 ? (y - y0) / dy : 0;
        if (dx > dy) {
            if (leftBuffer == null || rightBuffer == null) {
                bresenhamDrawAlgorithm(img, dx, dy, sx, sy, x0, y0, color, false);
            } else {
                bresenhamScanLineAlgorithm((int)img.getHeight(), dx, dy, sx, sy, x0, y0, false, leftBuffer, rightBuffer);
            }
        } else {
            if (leftBuffer == null || rightBuffer == null) {
                bresenhamDrawAlgorithm(img, dy, dx, sy, sx, y0, x0, color, true);
            } else {
                bresenhamScanLineAlgorithm((int)img.getHeight(), dy, dx, sy, sx, y0, x0, true, leftBuffer, rightBuffer);
            }
        }
    }

    private static void setPixel(WritableImage img, int imgWidth, int imgHeight, int x, int y, Color color) {
        if (x >= 0 && x < imgWidth && y >= 0 && y < imgHeight) {
            img.getPixelWriter().setColor(x, y, color);
        }
    }

    private static void bresenhamDrawAlgorithm(WritableImage img, int dx, int dy, int sx, int sy, int x, int y,
                                               Color color, boolean invertedCoords) {
        int imgHeight = (int)img.getHeight();
        int imgWidth = (int)img.getWidth();
        int err = -dx;
        if (invertedCoords) {
            setPixel(img, imgWidth, imgHeight, y, x, color);
        } else {
            setPixel(img, imgWidth, imgHeight, x, y, color);
        }
        for (int i = 0; i < dx; ++i) {
            x += sx;
            err += 2 * dy;
            if (err > 0) {
                err -= 2 * dx;
                y += sy;
            }
            if (invertedCoords) {
                setPixel(img, imgWidth, imgHeight, y, x, color);
            }
            else {
                setPixel(img, imgWidth, imgHeight, x, y, color);;
            }
        }
    }

    private static void bresenhamScanLineAlgorithm(int imgHeight, int dx, int dy, int sx, int sy, int x, int y,
                                                   boolean invertedCoords, int[] leftBuffer, int[] rightBuffer) {
        if (leftBuffer.length != imgHeight || rightBuffer.length != imgHeight) {
            return;
        }
        int err = -dx;
        for (int i = 0; i < dx; ++i) {
            x += sx;
            err += 2 * dy;
            if (err > 0) {
                err -= 2 * dx;
                y += sy;
            }
            if (invertedCoords) {
                if (x >= 0 && x < imgHeight) {
                    if (y < leftBuffer[x]) {
                        leftBuffer[x] = y;
                    }
                    if (y > rightBuffer[x]) {
                        rightBuffer[x] = y;
                    }
                }
            }
            else {
                if (y >= 0 && y < imgHeight) {
                    if (x < leftBuffer[y]) {
                        leftBuffer[y] = x;
                    }
                    if (x > rightBuffer[y]) {
                        rightBuffer[y] = x;
                    }
                }
            }
        }
    }

    private static void drawRectangle(WritableImage img, double x1, double y1, double x2, double y2,
                                     double x3, double y3, double x4, double y4, Color color) {
        int imgHeight = (int)img.getHeight();
        int[] leftBuffer = new int[imgHeight];
        int[] rightBuffer = new int[imgHeight];
        for (int i = 0; i < imgHeight; ++i) {
            leftBuffer[i] = Integer.MAX_VALUE;
            rightBuffer[i] = Integer.MIN_VALUE;
        }

        drawThinLine(img, x1, y1, x2, y2, Color.WHITE, leftBuffer, rightBuffer);
        drawThinLine(img, x2, y2, x3, y3, Color.WHITE, leftBuffer, rightBuffer);
        drawThinLine(img, x3, y3, x4, y4, Color.WHITE, leftBuffer, rightBuffer);
        drawThinLine(img, x4, y4, x1, y1, Color.WHITE, leftBuffer, rightBuffer);

        for (int i = 0; i < imgHeight; ++i) {
            if (leftBuffer[i] != Integer.MAX_VALUE || rightBuffer[i] != Integer.MIN_VALUE) {
                for (int x = leftBuffer[i]; x <= rightBuffer[i]; ++x) {
                    setPixel(img, (int)img.getWidth(), imgHeight, x, i, color);
                }
            }
        }
    }

    private static void drawThickLine(WritableImage img, double x1, double y1, double x2, double y2, Color color, int thickness) {
        double rx1, ry1, rx2, ry2, rx3, ry3, rx4, ry4, dx, dy;
        if (x2 - x1 == 0) {
            dx = thickness - 1;
            dy = 0;
        } else if (y1 - y2 == 0) {
            dx = 0;
            dy = thickness - 1;
        } else {
            double k1 = (y2 - y1) / (x2 - x1);
            double k2 = -(1.0 / k1);
            dx = Math.sqrt(Math.pow(thickness, 2) / (1 + Math.pow(k2, 2)));
            dy = dx * k2;
            dx = Math.floor(dx);
            dy = Math.floor(dy);
            if (dy < 0) {
                dy += 1;
            }
        }

        rx1 = x1 + dx / 2;
        ry1 = y1 + dy / 2;
        rx2 = x1 - dx / 2;
        ry2 = y1 - dy / 2;
        rx3 = x2 - dx / 2;
        ry3 = y2 - dy / 2;
        rx4 = x2 + dx / 2;
        ry4 = y2 + dy / 2;
        drawRectangle(img, rx1, ry1, rx2, ry2, rx3, ry3, rx4, ry4, color);
    }

    public static void drawLine(WritableImage img, double x1, double y1, double x2, double y2, Color color,
                                int thickness) throws IllegalArgumentException {
        if (thickness == 1) {
            drawThinLine(img, x1, y1, x2, y2, color, null, null);
        } else if (thickness > 1 && thickness <= 100) {
            drawThickLine(img, x1, y1, x2, y2, color, thickness);
        } else {
            throw new IllegalArgumentException();
        }
    }
}

package ru.nsu.cg;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Stack;

public class PaintGraphics {
    private static class Span {
        private final int x;
        private final int y;
        public Span(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
    }

    private static void findSpans(Image img, int x1, int x2, int y, Stack<Span> spanStack, Color oldColor) {
        boolean spanAdded = false;
        for (int x = x1; x <= x2; ++x) {
            if (!img.getPixelReader().getColor(x, y).equals(oldColor)) {
                spanAdded = false;
            } else if (!spanAdded) {
                spanStack.push(new Span(x, y));
                spanAdded = true;
            }
        }
    }

    public static void fill(WritableImage img, double _seedX, double _seedY, Color color) {
        int seedX = (int)Math.round(_seedX);
        int seedY = (int)Math.round(_seedY);
        Color oldColor = img.getPixelReader().getColor(seedX, seedY);
        if (oldColor.equals(color)) {
            return;
        }
        Span startSpan = new Span(seedX, seedY);
        Stack<Span> spanStack = new Stack<>();
        spanStack.add(startSpan);
        while (!spanStack.empty()) {
            Span nextSpan = spanStack.pop();
            final int y = nextSpan.getY();
            int rightX = nextSpan.getX();
            int leftX = nextSpan.getX() - 1;
            while (leftX >= 0 && img.getPixelReader().getColor(leftX, y).equals(oldColor)) {
                img.getPixelWriter().setColor(leftX, y, color);
                --leftX;
            }
            while (rightX < img.getWidth() && img.getPixelReader().getColor(rightX, y).equals(oldColor)) {
                img.getPixelWriter().setColor(rightX, y, color);
                ++rightX;
            }
            if (y > 0) {
                findSpans(img, leftX + 1, rightX - 1, y - 1, spanStack, oldColor);
            }
            if (y < img.getHeight() - 1) {
                findSpans(img, leftX + 1, rightX - 1, y + 1, spanStack, oldColor);
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

    public static void drawRectangle(WritableImage img, double x1, double y1, double x2, double y2,
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
        } else if (thickness > 1 && thickness <= PaintSettings.maxThickness) {
            drawThickLine(img, x1, y1, x2, y2, color, thickness);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void drawPolygon(WritableImage img, double centerX, double centerY,
                                   int vertexNum, int radius, int rotate, Color color) {
        if (vertexNum < PaintSettings.minStampVertexNum || vertexNum > PaintSettings.maxStampVertexNum ||
        radius < PaintSettings.minStampSize || radius > PaintSettings.maxStampSize) {
            return;
        }
        Point2D[] vertices = new Point2D[vertexNum];
        for (int i = 0; i < vertexNum; ++i) {
            double angle = Math.toRadians(i * (360.0 / vertexNum) + 90 + rotate);
            double x = centerX + radius * Math.cos(angle);
            double y = centerY - radius * Math.sin(angle);
            vertices[i] = new Point2D(x, y);
        }
        for (int i = 0; i < vertexNum - 1; ++i) {
            drawThinLine(img, vertices[i].getX(), vertices[i].getY(), vertices[i + 1].getX(), vertices[i + 1].getY(),
                    color, null, null);
        }
        drawThinLine(img, vertices[vertexNum - 1].getX(), vertices[vertexNum - 1].getY(), vertices[0].getX(),
                vertices[0].getY(), color, null, null);
    }

    public static void drawStar(WritableImage img, double centerX, double centerY,
                                int vertexNum, int radius, int rotate, Color color) {
        if (vertexNum < PaintSettings.minStampVertexNum || vertexNum > PaintSettings.maxStampVertexNum ||
                radius < PaintSettings.minStampSize || radius > PaintSettings.maxStampSize) {
            return;
        }
        int innerRadius = radius / 2;
        Point2D[] vertices = new Point2D[vertexNum * 2];
        for (int i = 0; i < vertexNum * 2; ++i) {
            int nextRadius = i % 2 == 0 ? radius : innerRadius;
            double angle = Math.toRadians(i * (360.0 / (vertexNum * 2)) + 90 + rotate);
            double x = centerX + nextRadius * Math.cos(angle);
            double y = centerY - nextRadius * Math.sin(angle);
            vertices[i] = new Point2D(x, y);
        }
        for (int i = 0; i < 2 * vertexNum - 1; ++i) {
            drawThinLine(img, vertices[i].getX(), vertices[i].getY(), vertices[i + 1].getX(), vertices[i + 1].getY(),
                    color, null, null);
        }
        drawThinLine(img, vertices[2 * vertexNum - 1].getX(), vertices[2 * vertexNum - 1].getY(), vertices[0].getX(),
                vertices[0].getY(), color, null, null);
    }
}

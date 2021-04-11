package ru.nsu.cg.model;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Stack;

public class Graphics {
    private static final double isolineEpsilon = 0.01;

    public static final int maxThickness = 10;

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

    private static double clamp(double min, double max, double val) {
        if (val < min) {
            return min;
        } else {
            return Math.min(val, max);
        }
    }

    public static void fill(WritableImage img, double _seedX, double _seedY, Color color) {
        _seedX = clamp(0, img.getWidth() - 1, _seedX);
        _seedY = clamp(0, img.getHeight() - 1, _seedY);
        int seedX = (int)Math.round(_seedX);
        int seedY = (int)Math.round(_seedY);
        Color oldColor = img.getPixelReader().getColor(seedX, seedY);
        if (oldColor.equals(color) || !oldColor.equals(Color.TRANSPARENT)) {
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

    public static void drawGrid(WritableImage img, int gridWidth, int gridHeight, int thickness, Color color) {
        double deltaGridWidth = img.getWidth() / (gridWidth - 1);
        double deltaGridHeight = img.getHeight() / (gridHeight - 1);
        for (int i = 0; i < gridWidth; ++i) {
            drawLine(img, i * deltaGridWidth, 0, i * deltaGridWidth, img.getHeight() - 1, color, thickness);
        }
        for (int i = 0; i < gridHeight; ++i) {
            drawLine(img, 0, i * deltaGridHeight, img.getWidth() - 1, i * deltaGridHeight, color, thickness);
        }
    }

    public static void drawColorMap(WritableImage img, final double[][] grid, Color[] colors) {
        if (colors.length < 2) {
            throw new RuntimeException();
        }
        double min = Function.gridMin(grid);
        double max = Function.gridMax(grid);
        double[] colorThresholds = new double[colors.length + 1];
        double delta = (max - min) / (colorThresholds.length - 1);
        for (int i = 0; i < colorThresholds.length; ++i) {
            colorThresholds[i] = min + i * delta;
        }
        double deltaWidth = img.getWidth() / (grid.length - 1);
        double deltaHeight = img.getHeight() / (grid[0].length - 1);
        for (int i = 0; i < colors.length - 1; ++i) {
            double isoValue = min + (i + 1) * ((max - min) / colors.length);
            drawIsoline(img, colors[i], 1, isoValue, grid);
        }

        for (int y = 0; y < grid.length; ++y) {
            for (int x = 0; x < grid[0].length; ++x) {
                double value = grid[y][x];
                for (int i = 0; i < colorThresholds.length - 1; ++i) {
                    if (colorThresholds[i] <= value && value <= colorThresholds[i + 1]) {
                        fill(img, x * deltaWidth, y * deltaHeight, colors[i]);
                        break;
                    }
                }
            }
        }
    }

    public static void drawSmoothMap(WritableImage img, double[][] grid, Color[] colors) {
        if (colors.length < 2) {
            throw new RuntimeException();
        }
        double min = Function.gridMin(grid);
        double max = Function.gridMax(grid);
        int colorsNum = colors.length;
        double[] colorThresholds = new double[colorsNum];
        double delta = (max - min) / (colorThresholds.length - 1);
        for (int i = 0; i < colorThresholds.length; ++i) {
            colorThresholds[i] = min + i * delta;
        }
        for (int y = 0; y < img.getHeight(); ++y) {
            for (int x = 0; x < img.getWidth(); ++x) {
                double value = Interpolation.bilinear(x, y, img.getWidth(), img.getHeight(), grid);
                for (int i = 0; i < colorThresholds.length - 1; ++i) {
                    if (colorThresholds[i] <= value && value <= colorThresholds[i + 1]) {
                        Color resColor = Interpolation.linear(colorThresholds[i], colorThresholds[i + 1],
                                colors[i], colors[i + 1], value);
                        img.getPixelWriter().setColor(x, y, resColor);
                        break;
                    }
                }
            }
        }
    }

    public static void drawSmoothLegend(WritableImage img, Color[] colors) {
        int colorsNum = colors.length;
        double delta = img.getWidth() / (colorsNum - 1);
        for (int i = 0; i < colorsNum - 1; ++i) {
            double startX = i * delta;
            double endX = (i + 1) * delta;
            for (double x = startX; x <= endX; ++x) {
                Color c = Interpolation.linear(startX, endX, colors[i], colors[i + 1], x);
                drawLine(img, x, 0, x, img.getHeight() - 1, c,1);
            }
            drawLine(img, startX, 0, startX, img.getHeight() - 1, Color.BLACK, 1);
        }
        drawLine(img, img.getWidth() - 1, 0, img.getWidth() - 1, img.getHeight() - 1, Color.BLACK, 1);
    }

    public static void drawColorMapLegend(WritableImage img, Color[] colors) {
        int colorsNum = colors.length;
        double delta = img.getWidth() / colorsNum;
        for (int i = 0; i < colorsNum; ++i) {
            double startX = i * delta;
            double endX = (i + 1) * delta;
            drawRectangle(img, startX, 0, startX, img.getHeight() - 1,
                    endX, img.getHeight() - 1, endX, 0, colors[i]);
            drawLine(img, startX, 0, startX, img.getHeight() - 1, Color.BLACK, 1);
        }
        drawLine(img, img.getWidth() - 1, 0, img.getWidth() - 1, img.getHeight() - 1, Color.BLACK, 1);
    }

    public static void drawIsolines(WritableImage img, int isolinesNum, Color color, double[][] grid, int thickness) {
        double min = Function.gridMin(grid);
        double max = Function.gridMax(grid);
        double delta = (max - min) / (isolinesNum + 1);
        for (int i = 0; i < isolinesNum; ++i) {
            drawIsoline(img, color, thickness, min + (i + 1) * delta, grid);
        }
    }

    // returns -1 if there is no isoline point
    private static double findIsolinePoint(double coord1, double coord2, double grid1, double grid2, double value, double eps) {
        double k = (value - grid1) * (value - grid2);
        if (k < 0) {
            return coord1 + (coord2 - coord1) * Math.abs((value - grid1) / (grid2 - grid1));
        } else if (value == grid1) {
            return coord1 + eps;
        } else if (value == grid2) {
            return coord2 - eps;
        } else {
            return -1;
        }
    }

    private static int countMarchingSquarePoints(double upX, double downX, double leftY, double rightY) {
        int result = 0;
        if (upX != -1) {
            ++result;
        }
        if (downX != -1) {
            ++result;
        }
        if (leftY != -1) {
            ++result;
        }
        if (rightY != -1) {
            ++result;
        }
        return result;
    }

    /*
    4 points in matrix: up, down, left, right
     */
    private static double[][] getMarchingSquarePoints(double upX, double downX, double leftY, double rightY,
                                                      double x1, double x2, double y1, double y2) {
        double[][] result = new double[countMarchingSquarePoints(upX, downX, leftY, rightY)][2];
        int i = 0;
        if (upX != -1) {
            result[i][0] = upX;
            result[i][1] = y1;
            ++i;
        }
        if (downX != -1) {
            result[i][0] = downX;
            result[i][1] = y2;
            ++i;
        }
        if (leftY != -1) {
            result[i][0] = x1;
            result[i][1] = leftY;
            ++i;
        }
        if (rightY != -1) {
            result[i][0] = x2;
            result[i][1] = rightY;
            ++i;
        }
        return result;
    }

    public static void drawIsoline(WritableImage img, Color color, int thickness, double value, double[][] grid) {
        int gridHeight = grid.length;
        if (gridHeight < 1) {
            return;
        }
        int gridWidth = grid[0].length;
        double deltaGridWidth = img.getWidth() / (gridWidth - 1);
        double deltaGridHeight = img.getHeight() / (gridHeight - 1);
        for (int y = 0; y < gridHeight - 1; ++y) {
            for (int x = 0; x < gridWidth - 1; ++x) {
                double x1 = x * deltaGridWidth;
                double x2 = (x + 1) * deltaGridWidth;
                double y1 = y * deltaGridHeight;
                double y2 = (y + 1) * deltaGridHeight;
                double upX = findIsolinePoint(x1, x2, grid[y][x], grid[y][x + 1], value, isolineEpsilon);
                double downX = findIsolinePoint(x1, x2, grid[y + 1][x], grid[y + 1][x + 1], value, isolineEpsilon);
                double leftY = findIsolinePoint(y1, y2 , grid[y][x], grid[y + 1][x], value, isolineEpsilon);
                double rightY = findIsolinePoint(y1, y2, grid[y][x + 1], grid[y + 1][x + 1], value, isolineEpsilon);
                double[][] points = getMarchingSquarePoints(upX, downX, leftY, rightY, x1, x2, y1, y2);
                if (points.length == 2) {
                    drawLine(img, points[0][0], points[0][1], points[1][0], points[1][1], color, thickness);
                } else if (points.length == 4) {
                    double centerValue = (grid[y][x] + grid[y + 1][x] + grid[y][x + 1] +
                            grid[y + 1][x + 1]) / 4;
                    double k = (centerValue - value) * (grid[y][x] - value);
                    if (k <= 0) {
                        double p1X = findIsolinePoint(x1, (x1 + x2) / 2, grid[y][x], centerValue, value, isolineEpsilon);
                        double p1Y = findIsolinePoint(y1, (y1 + y2) / 2, grid[y][x], centerValue, value, isolineEpsilon);
                        double p2X = findIsolinePoint((x1 + x2) / 2, x2, centerValue, grid[y + 1][x + 1], value, isolineEpsilon);
                        double p2Y = findIsolinePoint((y1 + y2) / 2, y2, centerValue, grid[y + 1][x + 1], value, isolineEpsilon);
                        drawLine(img, points[0][0], points[0][1], p1X, p1Y, color, thickness);
                        drawLine(img, points[2][0], points[2][1], p1X, p1Y, color, thickness);
                        drawLine(img, points[1][0], points[1][1], p2X, p2Y, color, thickness);
                        drawLine(img, points[3][0], points[3][1], p2X, p2Y, color, thickness);
                    } else {
                        double p1X = findIsolinePoint(x1, (x1 + x2) / 2, grid[y + 1][x], centerValue, value, isolineEpsilon);
                        double p1Y = findIsolinePoint((y1 + y2) / 2, y2, centerValue, grid[y + 1][x], value, isolineEpsilon);
                        double p2X = findIsolinePoint((x1 + x2) / 2, x2, centerValue, grid[y][x + 1], value, isolineEpsilon);
                        double p2Y = findIsolinePoint(y1, (y1 + y2) / 2, grid[y][x + 1], centerValue, value, isolineEpsilon);
                        drawLine(img, points[0][0], points[0][1], p2X, p2Y, color, thickness);
                        drawLine(img, points[3][0], points[3][1], p2X, p2Y, color, thickness);
                        drawLine(img, points[1][0], points[1][1], p1X, p1Y, color, thickness);
                        drawLine(img, points[2][0], points[2][1], p1X, p1Y, color, thickness);
                    }
                }
            }
        }
    }

    public static void blend(Image img1, Image img2, WritableImage result) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight() ||
        img2.getWidth() != result.getWidth() || img2.getHeight() != result.getHeight()) {
            return;
        }
        for (int y = 0; y < img1.getHeight(); ++y) {
            for (int x = 0; x < img2.getWidth(); ++x) {
                double alpha1 = img1.getPixelReader().getColor(x, y).getOpacity();
                double alpha2 = img2.getPixelReader().getColor(x, y).getOpacity();
                Color color1 = img1.getPixelReader().getColor(x, y);
                Color color2 = img2.getPixelReader().getColor(x, y);
                result.getPixelWriter().setColor(x, y, Color.color(
                        color1.getRed() * (1.0 - alpha2) + alpha2 * color2.getRed(),
                        color1.getGreen() * (1.0 - alpha2) + alpha2 * color2.getGreen(),
                        color1.getBlue() * (1.0 - alpha2) + alpha2 * color2.getBlue(),
                        alpha1 * (1.0 - alpha2) + alpha2 * alpha2
                ));
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
        } else if (thickness > 1 && thickness <= maxThickness) {
            drawThickLine(img, x1, y1, x2, y2, color, thickness);
        } else {
            throw new IllegalArgumentException();
        }
    }
}

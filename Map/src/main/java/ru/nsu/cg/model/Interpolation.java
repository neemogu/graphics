package ru.nsu.cg.model;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Interpolation {
    private static double bilinear(double ij, double i1j, double ij1, double i1j1, double alphaX, double alphaY) {
        double c1 = ij * (1 - alphaX) + i1j * alphaX;
        double c2 = ij1 * (1 - alphaX) + i1j1 * alphaX;
        return c1 * (1 - alphaY) + c2 * alphaY;
    }

    private static boolean isInBounds(Image img, double x, double y) {
        return !(x < 0 || x > img.getWidth() - 1 || y < 0 || y > img.getHeight() - 1);
    }

    public static Color bilinear(Image img, double x, double y, Color outColor) {
        int i = (int)Math.floor(x);
        int j = (int)Math.floor(y);
        double alphaX = x - i;
        double alphaY = y - j;

        double red = bilinear(
                isInBounds(img, i, j) ? img.getPixelReader().getColor(i, j).getRed() : outColor.getRed(),
                isInBounds(img, i + 1, j) ? img.getPixelReader().getColor(i + 1, j).getRed() : outColor.getRed(),
                isInBounds(img, i, j + 1) ? img.getPixelReader().getColor(i, j + 1).getRed() : outColor.getRed(),
                isInBounds(img, i + 1, j + 1) ? img.getPixelReader().getColor(i + 1, j + 1).getRed() : outColor.getRed(),
                alphaX, alphaY
        );
        double green = bilinear(
                isInBounds(img, i, j) ? img.getPixelReader().getColor(i, j).getGreen() : outColor.getGreen(),
                isInBounds(img, i + 1, j) ? img.getPixelReader().getColor(i + 1, j).getGreen() : outColor.getGreen(),
                isInBounds(img, i, j + 1) ? img.getPixelReader().getColor(i, j + 1).getGreen() : outColor.getGreen(),
                isInBounds(img, i + 1, j + 1) ? img.getPixelReader().getColor(i + 1, j + 1).getGreen() : outColor.getGreen(),
                alphaX, alphaY
        );
        double blue = bilinear(
                isInBounds(img, i, j) ? img.getPixelReader().getColor(i, j).getBlue() : outColor.getBlue(),
                isInBounds(img, i + 1, j) ? img.getPixelReader().getColor(i + 1, j).getBlue() : outColor.getBlue(),
                isInBounds(img, i, j + 1) ? img.getPixelReader().getColor(i, j + 1).getBlue() : outColor.getBlue(),
                isInBounds(img, i + 1, j + 1) ? img.getPixelReader().getColor(i + 1, j + 1).getBlue() : outColor.getBlue(),
                alphaX, alphaY
        );
        return Color.color(red, green, blue);
    }

    public static Color bilinear(Image img, double x, double y) {
        return bilinear(img, x, y, Color.WHITE);
    }

    public static double bilinear(double x, double y, double width, double height, double[][] grid) {
        int gridHeight = grid.length;
        if (gridHeight < 1) {
            return 0;
        }
        int gridWidth = grid[0].length;
        double deltaGridWidth = (width - 1) / (gridWidth - 1);
        double deltaGridHeight = (height - 1) / (gridHeight - 1);
        for (int gY = 0; gY < gridHeight - 1; ++gY) {
            for (int gX = 0; gX < gridWidth - 1; ++gX) {
                double x1 = gX * deltaGridWidth;
                double x2 = (gX + 1) * deltaGridWidth;
                double y1 = gY * deltaGridHeight;
                double y2 = (gY + 1) * deltaGridHeight;
                if (x1 <= x && x <= x2 && y1 <= y && y <= y2) {
                    double alphaX = (x - x1) / (x2 - x1);
                    double alphaY = (y - y1) / (y2 - y1);
                    return bilinear(grid[gY][gX], grid[gY][gX + 1], grid[gY + 1][gX], grid[gY + 1][gX + 1], alphaX, alphaY);
                }
            }
        }
        return 0;
    }

    public static Color nearest(Image img, double x, double y, Color outColor) {
        if (x < 0 || x > img.getWidth() - 1 || y < 0 || y > img.getHeight() - 1) {
            return outColor;
        }
        return img.getPixelReader().getColor((int)Math.round(x), (int)Math.round(y));
    }

    public static Color nearest(Image img, double x, double y) {
        return nearest(img, x, y, Color.WHITE);
    }

    public static double linear(double x1, double v1, double x2, double v2, double x) {
        if (x2 < x1) {
            return v1;
        } else if (x2 == x1) {
            return (v1 + v2) / 2;
        } else {
            return v1 + (x - x1) / (x2 - x1) * (v2 - v1);
        }
    }

    public static Color linear(double left, double right, Color leftColor, Color rightColor, double x) {
        double red = linear(left, leftColor.getRed(), right, rightColor.getRed(), x);
        double green = linear(left, leftColor.getGreen(), right, rightColor.getGreen(), x);
        double blue = linear(left, leftColor.getBlue(), right, rightColor.getBlue(), x);
        return Color.color(red, green, blue);
    }

    public enum InterpolationType { BILINEAR, NEAREST }
}

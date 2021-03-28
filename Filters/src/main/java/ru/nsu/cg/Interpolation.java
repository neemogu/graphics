package ru.nsu.cg;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Interpolation {
    private static double bilinearColor(double ij, double i1j, double ij1, double i1j1, double alphaX, double alphaY) {
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

        double red = bilinearColor(
                isInBounds(img, i, j) ? img.getPixelReader().getColor(i, j).getRed() : outColor.getRed(),
                isInBounds(img, i + 1, j) ? img.getPixelReader().getColor(i + 1, j).getRed() : outColor.getRed(),
                isInBounds(img, i, j + 1) ? img.getPixelReader().getColor(i, j + 1).getRed() : outColor.getRed(),
                isInBounds(img, i + 1, j + 1) ? img.getPixelReader().getColor(i + 1, j + 1).getRed() : outColor.getRed(),
                alphaX, alphaY
        );
        double green = bilinearColor(
                isInBounds(img, i, j) ? img.getPixelReader().getColor(i, j).getGreen() : outColor.getGreen(),
                isInBounds(img, i + 1, j) ? img.getPixelReader().getColor(i + 1, j).getGreen() : outColor.getGreen(),
                isInBounds(img, i, j + 1) ? img.getPixelReader().getColor(i, j + 1).getGreen() : outColor.getGreen(),
                isInBounds(img, i + 1, j + 1) ? img.getPixelReader().getColor(i + 1, j + 1).getGreen() : outColor.getGreen(),
                alphaX, alphaY
        );
        double blue = bilinearColor(
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

    public static Color nearest(Image img, double x, double y, Color outColor) {
        if (x < 0 || x > img.getWidth() - 1 || y < 0 || y > img.getHeight() - 1) {
            return outColor;
        }
        return img.getPixelReader().getColor((int)Math.round(x), (int)Math.round(y));
    }

    public static Color nearest(Image img, double x, double y) {
        return nearest(img, x, y, Color.WHITE);
    }
}

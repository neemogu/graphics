package ru.nsu.cg;

import javafx.scene.image.WritableImage;

import java.io.Writer;

public class ImageTransforms {
    public static final int minDimensionSize = 5;
    public static final int maxDimensionSize = 8000;
    public static final int minAngle = -180;
    public static final int maxAngle = 180;

    public static void resizeImage(WritableImage srcImage, WritableImage destImage, FiltersSettings.InterpType interpolationType) {
        double srcWidth = srcImage.getWidth();
        double srcHeight = srcImage.getHeight();
        int destWidth = (int)destImage.getWidth();
        int destHeight = (int)destImage.getHeight();
        if (srcWidth == destWidth && srcHeight == destHeight) {
            destImage.getPixelWriter().setPixels(0, 0, destWidth, destHeight, srcImage.getPixelReader(), 0, 0);
            return;
        }
        double scaleX = srcWidth / destWidth;
        double scaleY = srcHeight / destHeight;
        for (int y = 0; y < destHeight; ++y) {
            for (int x = 0; x < destWidth; ++x) {
                switch (interpolationType) {
                    case BILINEAR:
                        destImage.getPixelWriter().setColor(x, y,
                                Interpolation.bilinear(srcImage, x * scaleX, y * scaleY));
                        break;
                    case NEAREST:
                        destImage.getPixelWriter().setColor(x, y,
                                Interpolation.nearest(srcImage, x * scaleX, y * scaleY));
                        break;
                }
            }
        }
    }

    public static void rotateImage(WritableImage image, double angleDegrees, FiltersSettings.InterpType interpolationType) {
        double centerX = image.getWidth() / 2;
        double centerY = image.getHeight() / 2;
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();
        double angleRadians = Math.toRadians(angleDegrees);
        WritableImage rotatedImage = new WritableImage(width, height);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                double centeredX = x - centerX;
                double centeredY = y - centerY;

                double rotatedX = centeredX * Math.cos(angleRadians) + centeredY * Math.sin(angleRadians);
                double rotatedY = centeredY * Math.cos(angleRadians) - centeredX * Math.sin(angleRadians);

                double sourceX = rotatedX + centerX;
                double sourceY = rotatedY + centerY;

                switch (interpolationType) {
                    case BILINEAR:
                        rotatedImage.getPixelWriter().setColor(x, y,
                                Interpolation.bilinear(image, sourceX, sourceY));
                        break;
                    case NEAREST:
                        rotatedImage.getPixelWriter().setColor(x, y,
                                Interpolation.nearest(image, sourceX, sourceY));
                        break;
                }

            }
        }
        image.getPixelWriter().setPixels(0, 0, width, height, rotatedImage.getPixelReader(), 0, 0);
    }

}

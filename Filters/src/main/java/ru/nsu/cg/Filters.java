package ru.nsu.cg;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.Arrays;

public class Filters {
    public static final double maxGamma = 10.0;
    public static final double minGamma = 0.1;
    public static final int minGaussianSize = 3;
    public static final int maxGaussianSize = 11;
    public static final int maxMedianSize = 11;
    public static final int minMedianSize = 3;
    public static final double maxBrightness = 0.5;
    public static final double minBrightness = -0.5;
    public static final double maxContrast = 10.0;
    public static final double minContrast = 0.1;
    public static final double minRobertsThreshold = 0.0;
    public static final double maxRobertsThreshold = 1.5;
    public static final double minSobelThreshold = 0.0;
    public static final double maxSobelThreshold = 2.5;
    public static final int minColorQuantity = 2;
    public static final int maxColorQuantity = 128;
    public static final int maxColorThreshold = 255;
    public static final int minColorThreshold = 0;


    public static void greyscale(WritableImage img) {
        for (int i = 0; i < img.getWidth(); ++i) {
            for (int j = 0; j < img.getHeight(); ++j) {
                Color color = img.getPixelReader().getColor(i, j);
                double greyValue = color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114;
                Color grey = Color.color(greyValue, greyValue, greyValue);
                img.getPixelWriter().setColor(i, j, grey);
            }
        }
    }

    public static void negative(WritableImage img) {
        for (int i = 0; i < img.getWidth(); ++i) {
            for (int j = 0; j < img.getHeight(); ++j) {
                Color color = img.getPixelReader().getColor(i, j);
                img.getPixelWriter().setColor(i, j, color.invert());
            }
        }
    }

    private static Color saturate(NotSaturatedColor color) {
        return Color.color(clampColorComponent(color.red), clampColorComponent(color.green),
                clampColorComponent(color.blue));
    }

    private static Color saturate(double red, double green, double blue) {
        return Color.color(clampColorComponent(red), clampColorComponent(green),
                clampColorComponent(blue));
    }

    private static int clamp(int value, int maxValue) {
        if (value < 0) {
            return 0;
        }
        else return Math.min(value, maxValue);
    }

    private static double clampColorComponent(double colorComponent) {
        if (colorComponent < 0.0) {
            return 0.0;
        }
        else return Math.min(colorComponent, 1.0);
    }

    private static class NotSaturatedColor {
        public double red;
        public double green;
        public double blue;
        private NotSaturatedColor(double red, double green, double blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }

    private static NotSaturatedColor pixelKernel(WritableImage img, int i, int j, double[] kernel, int kernelSize) {
        double red = 0.0;
        double green = 0.0;
        double blue = 0.0;
        for (int k = 0; k < kernelSize; ++k) {
            for (int s = 0; s < kernelSize; ++s) {
                int x = clamp(i - kernelSize / 2 + s, (int)img.getWidth() - 1);
                int y = clamp(j - kernelSize / 2 + k, (int)img.getHeight() - 1);
                Color color = img.getPixelReader().getColor(x, y);
                red += kernel[k * kernelSize + s] * color.getRed();
                green += kernel[k * kernelSize + s] * color.getGreen();
                blue += kernel[k * kernelSize + s] * color.getBlue();
            }
        }
        return new NotSaturatedColor(red, green, blue);
    }

    private static void kernel(WritableImage img, double[] kernel) {
        int kernelSize = (int)Math.sqrt(kernel.length);
        WritableImage newImg = new WritableImage((int)img.getWidth(), (int)img.getHeight());
        for (int i = 0; i < img.getHeight(); ++i) {
            for (int j = 0; j < img.getWidth(); ++j) {
                newImg.getPixelWriter().setColor(j, i, saturate(pixelKernel(img, j, i, kernel, kernelSize)));
            }
        }
        img.getPixelWriter().setPixels(0, 0, (int)img.getWidth(), (int)img.getHeight(),
                newImg.getPixelReader(), 0, 0);
    }

    private static final double[] gaussian3Matrix = {
            1.0 / 16, 2.0 / 16, 1.0 / 16,
            2.0 / 16, 4.0 / 16, 2.0 / 16,
            1.0 / 16, 2.0 / 16, 1.0 / 16
    };

    private static void gaussian3(WritableImage img) {
        kernel(img, gaussian3Matrix);
    }

    private static final double[] gaussian5Matrix = {
            1.0 / 256, 4.0 / 256, 6.0 / 256, 4.0 / 256, 1.0 / 256,
            4.0 / 256, 16.0 / 256, 24.0 / 256, 16.0 / 256, 4.0 / 256,
            6.0 / 256, 24.0 / 256, 36.0 / 256, 24.0 / 256, 6.0 / 256,
            4.0 / 256, 16.0 / 256, 24.0 / 256, 16.0 / 256, 4.0 / 256,
            1.0 / 256, 4.0 / 256, 6.0 / 256, 4.0 / 256, 1.0 / 256
    };

    private static void gaussian5(WritableImage img) {
        kernel(img, gaussian5Matrix);
    }

    private static void gaussianBig(WritableImage img, int size) {
        int matrixSize = size * size;
        double[] matrix = new double[matrixSize];
        for (int i = 0; i < matrixSize; ++i) {
            matrix[i] = 1.0 / matrixSize;
        }
        kernel(img, matrix);
    }

    public static void gaussian(WritableImage img, int size) {
        if (size == 3) {
            gaussian3(img);
        } else if (size == 5) {
            gaussian5(img);
        } else if (size >= 7 && size <= maxGaussianSize && size % 2 != 0) {
            gaussianBig(img, size);
        }
    }

    public static void gammaCorrection(WritableImage img, double gamma) {
        if (gamma < minGamma || gamma > maxGamma) {
            return;
        }
        double p = 1.0 / gamma;
        for (int i = 0; i < img.getWidth(); ++i) {
            for (int j = 0; j < img.getHeight(); ++j) {
                Color color = img.getPixelReader().getColor(i, j);
                img.getPixelWriter().setColor(i, j, saturate(
                        Math.pow(color.getRed(), p),
                        Math.pow(color.getGreen(), p),
                        Math.pow(color.getBlue(), p)
                ));
            }
        }
    }

    public static void brightness(WritableImage img, double brightness) {
        if (brightness < minBrightness || brightness > maxBrightness) {
            return;
        }
        for (int i = 0; i < img.getWidth(); ++i) {
            for (int j = 0; j < img.getHeight(); ++j) {
                Color color = img.getPixelReader().getColor(i, j);
                img.getPixelWriter().setColor(i, j, saturate(
                        color.getRed() + brightness,
                        color.getGreen() + brightness,
                        color.getBlue() + brightness
                ));
            }
        }
    }

    public static void contrast(WritableImage img, double contrast) {
        if (contrast < minContrast || contrast > maxContrast) {
            return;
        }
        for (int i = 0; i < img.getWidth(); ++i) {
            for (int j = 0; j < img.getHeight(); ++j) {
                Color color = img.getPixelReader().getColor(i, j);
                img.getPixelWriter().setColor(i, j, saturate(
                        (color.getRed() - 0.5) * contrast + 0.5,
                        (color.getGreen() - 0.5) * contrast + 0.5,
                        (color.getBlue() - 0.5) * contrast + 0.5
                ));
            }
        }
    }

    private static final double[] embossing = {
            0.0, 1.0, 0.0,
            -1.0, 0.0, 1.0,
            0.0, -1.0, 0.0
    };

    public static void embossing(WritableImage img) {
        kernel(img, embossing);
        for (int i = 0; i < img.getHeight(); ++i) {
            for (int j = 0; j < img.getWidth(); ++j) {
                Color color = img.getPixelReader().getColor(j, i);
                img.getPixelWriter().setColor(j, i, saturate(
                        color.getRed() + 0.5,
                        color.getGreen() + 0.5,
                        color.getBlue() + 0.5
                ));
            }
        }
    }

    private static final double[] sharpening = {
            0.0, -1.0, 0.0,
            -1.0, 5.0, -1.0,
            0.0, -1.0, 0.0
    };

    public static void sharpening(WritableImage img) {
        kernel(img, sharpening);
    }

    private static double getNearestThreshold(double[] thresholds, double value) {
        for (int i = 0; i < thresholds.length - 1; ++i) {
            if (value >= thresholds[i] && value <= thresholds[i + 1]) {
                return value - thresholds[i] < thresholds[i + 1] - value ? thresholds[i] : thresholds[i + 1];
            }
        }
        return thresholds[0];
    }

    private enum DitheringType { FLOYD, ORDERED };

    private static void dithering(WritableImage img, DitheringType type,
                                 int redQuantity, int greenQuantity, int blueQuantity,
                                  double[] ODMatrix) {
        int width = (int)img.getWidth();
        int height = (int)img.getHeight();
        double[] redComponent = new double[width * height];
        double[] greenComponent = new double[width * height];
        double[] blueComponent = new double[width * height];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                Color color = img.getPixelReader().getColor(j,i);
                redComponent[i * width + j] = color.getRed();
                greenComponent[i * width + j] = color.getGreen();
                blueComponent[i * width + j] = color.getBlue();
            }
        }
        if (type == DitheringType.FLOYD) {
            floydDitheringColor(redComponent, width, height, redQuantity);
            floydDitheringColor(greenComponent, width, height, greenQuantity);
            floydDitheringColor(blueComponent, width, height, blueQuantity);
        } else if (type == DitheringType.ORDERED) {
            orderedDitheringColor(redComponent, width, height, redQuantity, ODMatrix);
            orderedDitheringColor(greenComponent, width, height, greenQuantity, ODMatrix);
            orderedDitheringColor(blueComponent, width, height, blueQuantity, ODMatrix);
        }
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                img.getPixelWriter().setColor(j, i, Color.color(redComponent[i * width + j],
                        greenComponent[i * width + j], blueComponent[i * width + j]));
            }
        }
    }

    private static void floydDitheringColor(double[] singleColorImage, int width, int height, int quantity) {
        double[] thresholds = new double[quantity];
        for (int i = 0; i < thresholds.length; ++i) {
            thresholds[i] = i * (1.0 / (quantity - 1));
        }
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                double color = singleColorImage[i * width + j];
                double nearestThreshold = getNearestThreshold(thresholds, color);
                double error = color - nearestThreshold;
                singleColorImage[i * width + j] = nearestThreshold;
                if (i + 1 < height && j + 1 < width) {
                    singleColorImage[(i + 1) * width + j + 1] = clampColorComponent(
                            singleColorImage[(i + 1) * width + j + 1] + 1.0 / 16 * error);
                }
                if (i + 1 < height) {
                    singleColorImage[(i + 1) * width + j] = clampColorComponent(
                            singleColorImage[(i + 1) * width + j] + 5.0 / 16 * error);
                }
                if (j + 1 < width) {
                    singleColorImage[i * width + j + 1] = clampColorComponent(
                            singleColorImage[i * width + j + 1] + 7.0 / 16 * error);
                }
                if (i + 1 < height && j - 1 >= 0) {
                    singleColorImage[(i + 1) * width + j - 1] = clampColorComponent(
                            singleColorImage[(i + 1) * width + j - 1] + 3.0 / 16 * error);
                }
            }
        }
    }

    public static void floydDithering(WritableImage img, int redQuantity, int greenQuantity, int blueQuantity) {
        if (blueQuantity < minColorQuantity || blueQuantity > maxColorQuantity || greenQuantity < minColorQuantity ||
                greenQuantity > maxColorQuantity || redQuantity < minColorQuantity || redQuantity > maxColorQuantity) {
            return;
        }
        dithering(img, DitheringType.FLOYD, redQuantity, greenQuantity, blueQuantity, null);
    }

    private static double[] generateODMatrix(int size) {
        double[] plusMatrix = {0, 2, 3, 1};
        double[] matrix = {0};
        for (int k = 2; k <= size; k *= 2) {
            int prevSize = k / 2;
            double[] nextStepMatrix = new double[k * k];
            for (int i = 0; i < k; ++i) {
                for (int j = 0; j < k; ++j) {
                    nextStepMatrix[i * k + j] = 4 * matrix[i % prevSize * prevSize + j % prevSize] +
                            plusMatrix[i / prevSize * 2 + j / prevSize];
                }
            }
            matrix = nextStepMatrix;
        }
        for (int i = 0; i < matrix.length; ++i) {
            matrix[i] /= (size * size);
        }
        return matrix;
    }

    private static int chooseMatrixSize(int maxQuantity) {
        if (maxQuantity <= 16) {
            return 8;
        } else if (maxQuantity <= 64) {
            return 4;
        } else {
            return 2;
        }
    }

    private static void orderedDitheringColor(double[] singleColorImage, int width, int height, int quantity,
                                              double[] ODMatrix) {
        double[] thresholds = new double[quantity];
        for (int i = 0; i < thresholds.length; ++i) {
            thresholds[i] = i * (1.0 / (quantity - 1));
        }
        int matrixSize = (int)Math.sqrt(ODMatrix.length);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int matrixX = j % matrixSize;
                int matrixY = i % matrixSize;
                double matrixValue = ODMatrix[matrixY * matrixSize + matrixX];
                double colorComponent = singleColorImage[i * width + j];
                for (int k = 0; k < thresholds.length - 1; ++k) {
                    if (thresholds[k] <= colorComponent && colorComponent <= thresholds[k + 1]) {
                        if ((colorComponent - thresholds[k]) / (thresholds[k + 1] - thresholds[k]) >= matrixValue) {
                            singleColorImage[i * width + j] = thresholds[k + 1];
                        } else {
                            singleColorImage[i * width + j] = thresholds[k];
                        }
                        break;
                    }
                }
            }
        }
    }

    public static void orderedDithering(WritableImage img, int redQuantity, int greenQuantity, int blueQuantity) {
        if (blueQuantity < minColorQuantity || blueQuantity > maxColorQuantity || greenQuantity < minColorQuantity ||
                greenQuantity > maxColorQuantity || redQuantity < minColorQuantity || redQuantity > maxColorQuantity) {
            return;
        }
        int matrixSize = chooseMatrixSize(Math.max(redQuantity, Math.max(greenQuantity, blueQuantity)));
        double[] matrix = generateODMatrix(matrixSize);
        System.out.println(Arrays.toString(matrix));
        dithering(img, DitheringType.ORDERED, redQuantity, greenQuantity, blueQuantity, matrix);
    }

    private static final double[] roberts1 = {
            0.0, 0.0, 0.0,
            0.0, 1.0, 0.0,
            0.0, 0.0, -1.0
    };

    private static final double[] roberts2 = {
            0.0, 0.0, 0.0,
            0.0, 0.0, 1.0,
            0.0, -1.0, 0.0
    };

    private static final double[] sobel1 = {
            1.0, 0.0, -1.0,
            2.0, 0.0, -2.0,
            1.0, 0.0, -1.0
    };

    private static final double[] sobel2 = {
            1.0, 2.0, 1.0,
            0.0, 0.0, 0.0,
            -1.0, -2.0, -1.0
    };


    private static void edgeDetection(WritableImage img, double[] kernel1, double[] kernel2, double threshold) {
        WritableImage newImg = new WritableImage((int)img.getWidth(), (int)img.getHeight());
        for (int i = 0; i < img.getHeight(); ++i) {
            for (int j = 0; j < img.getWidth(); ++j) {
                NotSaturatedColor gxColor = pixelKernel(img, j, i, kernel1, 3);
                NotSaturatedColor gyColor = pixelKernel(img, j, i, kernel2, 3);
                double GRed = Math.abs(gxColor.red) + Math.abs(gyColor.red);
                double GGreen = Math.abs(gxColor.green) + Math.abs(gyColor.green);
                double GBlue = Math.abs(gxColor.blue) + Math.abs(gyColor.blue);
                newImg.getPixelWriter().setColor(j, i, saturate(
                        GRed < threshold ? 0.0 : GRed,
                        GGreen < threshold ? 0.0 : GGreen,
                        GBlue < threshold ? 0.0 : GBlue
                ));
            }
        }
        img.getPixelWriter().setPixels(0, 0, (int)img.getWidth(), (int)img.getHeight(),
                newImg.getPixelReader(), 0, 0);
    }

    public static void robertsEdge(WritableImage img, double threshold) {
        edgeDetection(img, roberts1, roberts2, threshold);
    }

    public static void sobelEdge(WritableImage img, double threshold) {
        edgeDetection(img, sobel1, sobel2, threshold);
    }

    public static void median(WritableImage img, int size) {
        if (size < minMedianSize || size > maxMedianSize || size % 2 == 0) {
            return;
        }
        int matrixSize = size * size;
        WritableImage newImg = new WritableImage((int)img.getWidth(), (int)img.getHeight());
        double[] red = new double[matrixSize];
        double[] green = new double[matrixSize];
        double[] blue = new double[matrixSize];
        for (int i = 0; i < img.getHeight(); ++i) {
            for (int j = 0; j < img.getWidth(); ++j) {
                for (int k = 0; k < size; ++k) {
                    for (int s = 0; s < size; ++s) {
                        int x = clamp(j - size / 2 + s, (int)img.getWidth() - 1);
                        int y = clamp(i - size / 2 + k, (int)img.getHeight() - 1);
                        Color color = img.getPixelReader().getColor(x, y);
                        red[k * size + s] = color.getRed();
                        blue[k * size + s] = color.getBlue();
                        green[k * size + s] = color.getGreen();
                    }
                }
                Arrays.sort(red);
                Arrays.sort(green);
                Arrays.sort(blue);
                newImg.getPixelWriter().setColor(j, i, Color.color(red[matrixSize / 2 + 1], green[matrixSize / 2 + 1],
                        blue[matrixSize / 2 + 1]));
            }
        }
        img.getPixelWriter().setPixels(0, 0, (int)img.getWidth(), (int)img.getHeight(),
                newImg.getPixelReader(), 0, 0);
    }

    public static void solarise(WritableImage img, int redThreshold, int greenThreshold, int blueThreshold) {
        if (redThreshold < minColorThreshold || redThreshold > maxColorThreshold ||
                greenThreshold < minColorThreshold || greenThreshold > maxColorThreshold ||
                blueThreshold < minColorThreshold || blueThreshold > maxColorThreshold) {
            return;
        }
        double redThresholdNormalized = redThreshold / 255.0;
        double greenThresholdNormalized = greenThreshold / 255.0;
        double blueThresholdNormalized = blueThreshold / 255.0;

        for (int i = 0; i < img.getWidth(); ++i) {
            for (int j = 0; j < img.getHeight(); ++j) {
                Color color = img.getPixelReader().getColor(i, j);
                double red = color.getRed() >= redThresholdNormalized ? color.getRed() : 1.0 - color.getRed();
                double green = color.getGreen() >= greenThresholdNormalized ? color.getGreen() : 1.0 - color.getGreen();
                double blue = color.getBlue() >= blueThresholdNormalized ? color.getBlue() : 1.0 - color.getBlue();
                img.getPixelWriter().setColor(i, j, Color.color(red, green, blue));
            }
        }
    }

    public static void watercolor(WritableImage img, int size) {
        if (size < minMedianSize || size > maxMedianSize || size % 2 == 0) {
            return;
        }
        median(img, size);
        sharpening(img);
    }
}

package ru.nsu.cg;

import java.util.HashSet;
import java.util.Set;

public class FiltersSettings {
    private int brightness = 0;
    private int savedBrightness = 0;

    private double contrast = 1.0;
    private double savedContrast = 1.0;

    private double gamma = 2.2;
    private double savedGamma = 2.2;

    private int gaussianSize = Filters.minGaussianSize;
    private int savedGaussianSize = Filters.minGaussianSize;

    private int watercolorSize = Filters.minMedianSize;
    private int savedWatercolorSize = Filters.minMedianSize;

    private double robertsThreshold = Filters.minRobertsThreshold;
    private double savedRobertsThreshold = Filters.minRobertsThreshold;

    private double sobelThreshold = Filters.minSobelThreshold;
    private double savedSobelThreshold = Filters.minSobelThreshold;

    private int floydRedQuantity = Filters.minColorQuantity;
    private int floydGreenQuantity = Filters.minColorQuantity;
    private int floydBlueQuantity = Filters.minColorQuantity;
    private int savedFloydRedQuantity = Filters.minColorQuantity;
    private int savedFloydGreenQuantity = Filters.minColorQuantity;
    private int savedFloydBlueQuantity = Filters.minColorQuantity;

    private int orderedRedQuantity = Filters.minColorQuantity;
    private int orderedGreenQuantity = Filters.minColorQuantity;
    private int orderedBlueQuantity = Filters.minColorQuantity;
    private int savedOrderedRedQuantity = Filters.minColorQuantity;
    private int savedOrderedGreenQuantity = Filters.minColorQuantity;
    private int savedOrderedBlueQuantity = Filters.minColorQuantity;

    private int solariseRedThreshold = Filters.minColorThreshold;
    private int solariseGreenThreshold = Filters.minColorThreshold;
    private int solariseBlueThreshold = Filters.minColorThreshold;
    private int savedSolariseRedThreshold = Filters.minColorThreshold;
    private int savedSolariseGreenThreshold = Filters.minColorThreshold;
    private int savedSolariseBlueThreshold = Filters.minColorThreshold;

    private int rotationAngle = 0;
    private int savedRotationAngle = 0;

    private int resizeWidth = 800;
    private int resizeHeight = 600;
    private int savedResizeWidth = 800;
    private int savedResizeHeight = 600;

    public enum InterpType { BILINEAR, NEAREST }

    public enum Filter {
        GREYSCALE, NEGATIVE, GAUSSIAN, GAMMA, BRIGHTNESS, CONTRAST, EMBOSSING, SHARPENING, FSD, OD,
        ROBERTS, SOBEL, WATERCOLOR, SOLARISE
    }

    private InterpType interpolationType = InterpType.BILINEAR;
    private InterpType savedInterpolationType = InterpType.BILINEAR;

    private Filter lastFilter = Filter.GREYSCALE;

    private final Set<Subscriber> subs = new HashSet<>();

    public FiltersSettings() {
        saveValues();
    }

    public void saveValues() {
        savedBrightness = brightness;
        savedContrast = contrast;
        savedGamma = gamma;
        savedGaussianSize = gaussianSize;
        savedWatercolorSize = watercolorSize;
        savedRobertsThreshold = robertsThreshold;
        savedSobelThreshold = sobelThreshold;
        savedFloydRedQuantity = floydRedQuantity;
        savedFloydGreenQuantity = floydGreenQuantity;
        savedFloydBlueQuantity = floydBlueQuantity;
        savedOrderedRedQuantity = orderedRedQuantity;
        savedOrderedGreenQuantity = orderedGreenQuantity;
        savedOrderedBlueQuantity = orderedBlueQuantity;
        savedInterpolationType = interpolationType;
        savedRotationAngle = rotationAngle;
        savedResizeWidth = resizeWidth;
        savedResizeHeight = resizeHeight;
        savedSolariseBlueThreshold = solariseRedThreshold;
        savedSolariseGreenThreshold = solariseGreenThreshold;
        savedSolariseBlueThreshold = solariseBlueThreshold;
    }

    public void recoverSavedValues() {
        brightness = savedBrightness;
        contrast = savedContrast;
        gamma = savedGamma;
        gaussianSize = savedGaussianSize;
        watercolorSize = savedWatercolorSize;
        robertsThreshold = savedRobertsThreshold;
        sobelThreshold = savedSobelThreshold;
        floydRedQuantity = savedFloydRedQuantity;
        floydGreenQuantity = savedFloydGreenQuantity;
        floydBlueQuantity = savedFloydBlueQuantity;
        orderedRedQuantity = savedOrderedRedQuantity;
        orderedGreenQuantity = savedOrderedGreenQuantity;
        orderedBlueQuantity = savedOrderedBlueQuantity;
        interpolationType = savedInterpolationType;
        rotationAngle = savedRotationAngle;
        resizeWidth = savedResizeWidth;
        resizeHeight = savedResizeHeight;
        solariseRedThreshold = savedSolariseRedThreshold;
        solariseGreenThreshold = savedSolariseGreenThreshold;
        solariseBlueThreshold = savedSolariseBlueThreshold;
        notifySubscribers();
    }

    public void addSubscriber(Subscriber sub) {
        subs.add(sub);
    }

    public Filter getLastFilter() {
        return lastFilter;
    }

    public void setLastFilter(Filter lastFilter) {
        this.lastFilter = lastFilter;
    }

    public void notifySubscribers() {
        for (Subscriber sub : subs) {
            sub.update();
        }
    }

    public void unsubscribe(Subscriber sub) {
        subs.remove(sub);
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        if (brightness >= Filters.minBrightness && brightness <= Filters.maxBrightness) {
            this.brightness = brightness;
            notifySubscribers();
        }
    }

    public double getContrast() {
        return contrast;
    }

    public void setContrast(double contrast) {
        if (contrast >= Filters.minContrast && contrast <= Filters.maxContrast) {
            this.contrast = contrast;
            notifySubscribers();
        }
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        if (gamma >= Filters.minGamma && gamma <= Filters.maxGamma) {
            this.gamma = gamma;
            notifySubscribers();
        }
    }

    public int getGaussianSize() {
        return gaussianSize;
    }

    public void setGaussianSize(int gaussianSize) {
        if (gaussianSize >= Filters.minGaussianSize && gaussianSize <= Filters.maxGaussianSize && gaussianSize % 2 != 0) {
            this.gaussianSize = gaussianSize;
            notifySubscribers();
        }
    }

    public int getWatercolorSize() {
        return watercolorSize;
    }

    public void setWatercolorSize(int watercolorSize) {
        if (watercolorSize >= Filters.minMedianSize && watercolorSize <= Filters.maxMedianSize && watercolorSize % 2 != 0) {
            this.watercolorSize = watercolorSize;
            notifySubscribers();
        }
    }

    public double getRobertsThreshold() {
        return robertsThreshold;
    }

    public void setRobertsThreshold(double robertsThreshold) {
        if (robertsThreshold >= Filters.minRobertsThreshold && robertsThreshold <= Filters.maxRobertsThreshold) {
            this.robertsThreshold = robertsThreshold;
            notifySubscribers();
        }
    }

    public double getSobelThreshold() {
        return sobelThreshold;
    }

    public void setSobelThreshold(double sobelThreshold) {
        if (sobelThreshold >= Filters.minSobelThreshold && sobelThreshold <= Filters.maxSobelThreshold) {
            this.sobelThreshold = sobelThreshold;
            notifySubscribers();
        }
    }

    public int getFloydRedQuantity() {
        return floydRedQuantity;
    }

    public int getFloydGreenQuantity() {
        return floydGreenQuantity;
    }

    public int getFloydBlueQuantity() {
        return floydBlueQuantity;
    }

    public int getOrderedRedQuantity() {
        return orderedRedQuantity;
    }

    public int getOrderedGreenQuantity() {
        return orderedGreenQuantity;
    }

    public int getOrderedBlueQuantity() {
        return orderedBlueQuantity;
    }

    public void setFloydRedQuantity(int quantity) {
        if (quantity >= Filters.minColorQuantity && quantity <= Filters.maxColorQuantity) {
            this.floydRedQuantity = quantity;
            notifySubscribers();
        }
    }

    public void setFloydGreenQuantity(int quantity) {
        if (quantity >= Filters.minColorQuantity && quantity <= Filters.maxColorQuantity) {
            this.floydGreenQuantity = quantity;
            notifySubscribers();
        }
    }

    public void setFloydBlueQuantity(int quantity) {
        if (quantity >= Filters.minColorQuantity && quantity <= Filters.maxColorQuantity) {
            this.floydBlueQuantity = quantity;
            notifySubscribers();
        }
    }

    public void setOrderedRedQuantity(int quantity) {
        if (quantity >= Filters.minColorQuantity && quantity <= Filters.maxColorQuantity) {
            this.orderedRedQuantity = quantity;
            notifySubscribers();
        }
    }

    public void setOrderedGreenQuantity(int quantity) {
        if (quantity >= Filters.minColorQuantity && quantity <= Filters.maxColorQuantity) {
            this.orderedGreenQuantity = quantity;
            notifySubscribers();
        }
    }

    public void setOrderedBlueQuantity(int quantity) {
        if (quantity >= Filters.minColorQuantity && quantity <= Filters.maxColorQuantity) {
            this.orderedBlueQuantity = quantity;
            notifySubscribers();
        }
    }

    public InterpType getInterpType() {
        return interpolationType;
    }

    public void setInterpType(InterpType interpolationType) {
        this.interpolationType = interpolationType;
        notifySubscribers();
    }

    public int getResizeWidth() {
        return resizeWidth;
    }

    public int getResizeHeight() {
        return resizeHeight;
    }

    public int getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(int rotationAngle) {
        if (rotationAngle >= ImageTransforms.minAngle && rotationAngle <= ImageTransforms.maxAngle) {
            this.rotationAngle = rotationAngle;
            notifySubscribers();
        }
    }

    public void setResizeWidth(int resizeWidth) {
        if (resizeWidth >= ImageTransforms.minDimensionSize && resizeWidth <= ImageTransforms.maxDimensionSize) {
            this.resizeWidth = resizeWidth;
            notifySubscribers();
        }
    }

    public void setResizeHeight(int resizeHeight) {
        if (resizeHeight >= ImageTransforms.minDimensionSize && resizeHeight <= ImageTransforms.maxDimensionSize) {
            this.resizeHeight = resizeHeight;
            notifySubscribers();
        }
    }

    public int getSolariseRedThreshold() {
        return solariseRedThreshold;
    }

    public int getSolariseGreenThreshold() {
        return solariseGreenThreshold;
    }

    public int getSolariseBlueThreshold() {
        return solariseBlueThreshold;
    }

    public void setSolariseRedThreshold(int threshold) {
        if (threshold >= Filters.minColorThreshold && threshold <= Filters.maxColorThreshold) {
            this.solariseRedThreshold = threshold;
            notifySubscribers();
        }
    }

    public void setSolariseGreenThreshold(int threshold) {
        if (threshold >= Filters.minColorThreshold && threshold <= Filters.maxColorThreshold) {
            this.solariseGreenThreshold = threshold;
            notifySubscribers();
        }
    }

    public void setSolariseBlueThreshold(int threshold) {
        if (threshold >= Filters.minColorThreshold && threshold <= Filters.maxColorThreshold) {
            this.solariseBlueThreshold = threshold;
            notifySubscribers();
        }
    }
}

package ru.nsu.cg;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FiltersModel {
    private final FiltersSettings settings = new FiltersSettings();;
    private boolean isImageOpened = false;
    private boolean isFilteredImage = false;
    private Image originalImage = null;
    private WritableImage filteredImage = null;
    private WritableImage tranformedImage = null;
    private boolean rotated = false;
    private int rotation = 0;
    private boolean scaled = false;
    private int scaledWidth;
    private int scaledHeight;
    private boolean canReset = false;
    private final ArrayList<Subscriber> subs = new ArrayList<>();
    private boolean isViewportSize = false;

    public boolean openImage(File imageFile) {
        if (imageFile == null) {
            return true;
        }
        BufferedImage img;
        try {
            img = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("Unable to open " + imageFile.getName());
            return false;
        }
        canReset = false;
        isImageOpened = true;
        originalImage = SwingFXUtils.toFXImage(img, null);
        filteredImage = SwingFXUtils.toFXImage(img, null);
        tranformedImage = SwingFXUtils.toFXImage(img, null);
        resetScale();
        resetRotation();
        notifySubscribers();
        return true;
    }

    public boolean canReset() {
        return canReset;
    }

    public boolean saveImage(File imageFile) {
        if (imageFile == null) {
            return true;
        }
        BufferedImage img = SwingFXUtils.fromFXImage(getTransformedImage(), null);
        try {
            ImageIO.write(img, "png", imageFile);
        } catch (IOException e) {
            System.err.println("Unable to save " + imageFile.getName());
            return false;
        }
        return true;
    }

    public void addSubscriber(Subscriber sub) {
        subs.add(sub);
    }

    public void notifySubscribers() {
        for (Subscriber sub : subs) {
            sub.update();
        }
    }

    public FiltersSettings getSettings() {
        return settings;
    }

    public boolean isImageOpened() {
        return isImageOpened;
    }

    public void changeImage() {
        isFilteredImage = !isFilteredImage;
        notifySubscribers();
    }

    public void resetFilters() {
        filteredImage.getPixelWriter().setPixels(0, 0, (int)filteredImage.getWidth(), (int)filteredImage.getHeight(),
                originalImage.getPixelReader(), 0, 0);
        resetScale();
        resetRotation();
        canReset = false;
        isViewportSize = false;
        isFilteredImage = false;
        notifySubscribers();
    }

    public void applyChanges() {
        isFilteredImage = true;
        canReset = true;
        notifySubscribers();
    }

    public void applyChangesWithoutUpdating() {
        isFilteredImage = true;
        canReset = true;
    }

    public WritableImage getImageToFilter() {
        return filteredImage;
    }

    private Image getTransformedImage() {
        if (!scaled && !rotated) {
            return filteredImage;
        }
        if (tranformedImage.getWidth() != scaledWidth || tranformedImage.getHeight() != scaledHeight) {
            tranformedImage = new WritableImage(scaledWidth, scaledHeight);
        }
        ImageTransforms.resizeImage(filteredImage, tranformedImage, settings.getInterpType());
        if (rotated) {
            ImageTransforms.rotateImage(tranformedImage, rotation, settings.getInterpType());
        }
        return tranformedImage;
    }

    public Image getImage() {
        if (isFilteredImage) {
            return getTransformedImage();
        } else {
            return originalImage;
        }
    }

    private void resetRotation() {
        rotated = false;
        rotation = 0;
    }

    private void resetScale() {
        scaled = false;
        scaledWidth = (int)filteredImage.getWidth();
        scaledHeight = (int)filteredImage.getHeight();
    }

    public void addRotation() {
        rotated = true;
        rotation += settings.getRotationAngle();
    }

    public void setScale() {
        scaledWidth = settings.getResizeWidth();
        scaledHeight = settings.getResizeHeight();
        scaled = true;
    }

    public void setOriginScale() {
        scaledWidth = (int)originalImage.getWidth();
        scaledHeight = (int)originalImage.getHeight();
    }

    public int getOriginWidth() {
        return (int)originalImage.getWidth();
    }
    public int getOriginHeight() {
        return (int)originalImage.getHeight();
    }

    public void setIsViewportSize(boolean isViewportSize) {
        this.isViewportSize = isViewportSize;
        notifySubscribers();
    }

    public boolean isViewportSize() {
        return isViewportSize;
    }
}

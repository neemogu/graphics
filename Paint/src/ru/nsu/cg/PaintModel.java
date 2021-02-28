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

public class PaintModel {
    public static final int minDimensionSize = 50;
    public static final int maxDimensionSize = 10000;
    private static final int startWidth = 640;
    private static final int startHeight = 480;
    private int newWidth = startWidth;
    private int newHeight = startHeight;
    private final PaintSettings settings;
    private WritableImage currentCanvas;
    private WritableImage prevCanvas;
    private double x1;
    private double y1;
    private final ArrayList<Subscriber> subs = new ArrayList<>();

    public PaintModel() {
        currentCanvas = new WritableImage(startWidth, startHeight);
        prevCanvas = new WritableImage(startWidth, startHeight);
        clear();
        settings = new PaintSettings();
    }

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
        currentCanvas = SwingFXUtils.toFXImage(img, null);
        prevCanvas = SwingFXUtils.toFXImage(img, null);
        newHeight = (int)currentCanvas.getHeight();
        newWidth = (int)currentCanvas.getWidth();
        notifySubscribers();
        return true;
    }

    public void setNewWidth(int newWidth) {
        if (newWidth >= minDimensionSize && newWidth <= maxDimensionSize) {
            this.newWidth = newWidth;
        }
    }

    public void setNewHeight(int newHeight) {
        if (newHeight >= minDimensionSize && newHeight <= maxDimensionSize) {
            this.newHeight = newHeight;
        }
    }

    public int getNewWidth() {
        return newWidth;
    }

    public int getNewHeight() {
        return newHeight;
    }

    public void resetNewSize() {
        newHeight = (int)currentCanvas.getHeight();
        newWidth = (int)currentCanvas.getWidth();
    }

    public void resizeImage() {
        if (newWidth == currentCanvas.getWidth() && newHeight == currentCanvas.getHeight()) {
            return;
        }
        prevCanvas = new WritableImage(newWidth, newHeight);
        clearCanvas(prevCanvas);
        prevCanvas.getPixelWriter().setPixels(0, 0, (int)Math.min(currentCanvas.getWidth(), newWidth),
                (int)Math.min(currentCanvas.getHeight(), newHeight), currentCanvas.getPixelReader(), 0, 0);
        currentCanvas = new WritableImage(newWidth, newHeight);
        synchronizeBuffers(prevCanvas, currentCanvas);
        notifySubscribers();
    }

    public boolean saveImage(File imageFile) {
        if (imageFile == null) {
            return true;
        }
        BufferedImage img = SwingFXUtils.fromFXImage(currentCanvas, null);
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
        settings.addSubscriber(sub);
    }

    public void notifySubscribers() {
        for (Subscriber sub : subs) {
            sub.update();
        }
    }

    public void setFirstCoordinates(double x, double y) {
        x1 = x;
        y1 = y;
    }

    public void setTemporarySecondCoordinates(double x, double y) {
        if (settings.getMode() == PaintMode.LINE) {
            synchronizeBuffers(prevCanvas, currentCanvas);
            PaintGraphics.drawLine(currentCanvas, x1, y1, x, y, settings.getColor(), settings.getLineThickness());
        }
    }

    private void synchronizeBuffers(WritableImage toRead, WritableImage toWrite) {
        if (toRead.getWidth() != toWrite.getWidth() || toRead.getHeight() != toWrite.getHeight()) {
            new Exception().printStackTrace();
        }
        toWrite.getPixelWriter().setPixels(0, 0, (int)toRead.getWidth(),
                (int)toRead.getHeight(), toRead.getPixelReader(), 0, 0);
    }

    public void setSecondCoordinates(double x, double y) {
        switch (settings.getMode()) {
            case LINE:
                synchronizeBuffers(prevCanvas, currentCanvas);
                PaintGraphics.drawLine(currentCanvas, x1, y1, x, y, settings.getColor(), settings.getLineThickness());
                break;
            case STAMP:
                if (settings.isStarStamp()) {
                    PaintGraphics.drawStar(currentCanvas, x, y, settings.getStampVertexNum(),
                            settings.getStampSize(), settings.getStampRotation(), settings.getColor());
                } else {
                    PaintGraphics.drawPolygon(currentCanvas, x, y, settings.getStampVertexNum(),
                            settings.getStampSize(), settings.getStampRotation(), settings.getColor());
                }
                break;
            case FILL:
                PaintGraphics.fill(currentCanvas, x, y, settings.getColor());
                break;
        }
        synchronizeBuffers(currentCanvas, prevCanvas);
    }

    public void clear() {
        clearCanvas(currentCanvas);
        clearCanvas(prevCanvas);
    }

    private void clearCanvas(WritableImage canvas) {
        for (int i = 0; i < canvas.getWidth(); ++i) {
            for (int j = 0; j < canvas.getHeight(); ++j) {
                canvas.getPixelWriter().setColor(i, j, Color.color(1.0, 1.0, 1.0));
            }
        }
    }

    public PaintSettings getSettings() {
        return settings;
    }

    public Image getImage() {
        return currentCanvas;
    }
}

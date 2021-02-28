package ru.nsu.cg.cursors;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import resources.ResourceLoader;

public class StampCursor {
    private final ImageCursor cursor;

    public StampCursor() {
        Image cursorImg = new Image(ResourceLoader.class.getResourceAsStream("stamp_cursor.png"));
        cursor = new ImageCursor(cursorImg, cursorImg.getWidth() / 2, cursorImg.getHeight());
    }

    public Cursor getCursor() {
        return cursor;
    }
}

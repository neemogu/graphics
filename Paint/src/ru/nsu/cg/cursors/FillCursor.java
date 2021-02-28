package ru.nsu.cg.cursors;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import resources.ResourceLoader;

public class FillCursor {
    private final ImageCursor cursor;

    public FillCursor() {
        Image cursorImg = new Image(ResourceLoader.class.getResourceAsStream("fill_cursor.png"));
        cursor = new ImageCursor(cursorImg, 10, cursorImg.getHeight() / 4 * 3);
    }

    public Cursor getCursor() {
        return cursor;
    }
}

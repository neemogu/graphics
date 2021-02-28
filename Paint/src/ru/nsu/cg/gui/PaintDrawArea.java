package ru.nsu.cg.gui;

import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import ru.nsu.cg.PaintModel;
import ru.nsu.cg.Subscriber;
import ru.nsu.cg.cursors.FillCursor;
import ru.nsu.cg.cursors.LineCursor;
import ru.nsu.cg.cursors.StampCursor;

public class PaintDrawArea extends ScrollPane implements Subscriber {
    private ImageView drawAreaView;
    private final PaintModel model;

    private final LineCursor lineCursor = new LineCursor();
    private final FillCursor fillCursor = new FillCursor();
    private final StampCursor stampCursor = new StampCursor();

    public PaintDrawArea(PaintModel model) {
        super();
        this.model = model;
        drawAreaView = new ImageView();
        drawAreaView.setImage(model.getImage());
        setContent(drawAreaView);
        setFocusTraversable(false);
    }

    @Override
    public void update() {
        drawAreaView.setImage(model.getImage());
        switch (model.getSettings().getMode()) {
            case LINE:
                setCursor(lineCursor.getCursor());
                break;
            case STAMP:
                setCursor(stampCursor.getCursor());
                break;
            case FILL:
                setCursor(fillCursor.getCursor());
                break;
        }
    }

    public void setMousePressedHandler(EventHandler<? super MouseEvent> eventHandler) {
        drawAreaView.setOnMousePressed(eventHandler);
    }

    public void setMouseReleasedHandler(EventHandler<? super MouseEvent> eventHandler) {
        drawAreaView.setOnMouseReleased(eventHandler);
    }

    public void setMouseDraggedHandler(EventHandler<? super MouseEvent> eventHandler) {
        drawAreaView.setOnMouseDragged(eventHandler);
    }
}

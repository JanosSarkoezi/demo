package graph.core.adapter;

import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;

public class ResizeHandle {
    private final Rectangle rect;
    private final String position;

    public static final double HANDLE_SIZE = 8.0;

    public ResizeHandle(String position, Cursor cursor, Group parent) {
        this.position = position;
        this.rect = new Rectangle(8, 8, Color.WHITE);
        rect.setStroke(Color.DARKBLUE);
        rect.setCursor(cursor);
        rect.setUserData(position);
        parent.getChildren().add(rect);
    }

    public Rectangle getNode() { return rect; }
    public String getPosition() { return position; }
}

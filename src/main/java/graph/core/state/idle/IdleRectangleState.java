package graph.core.state.idle;

import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.state.active.MoveState;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class IdleRectangleState implements EditorState {

    @Override
    public void handleMousePressed(MouseEvent event, StateContext context) {
        if (event.getTarget() instanceof Shape rect) {
            context.setCurrentState(new MoveState(rect, event.getX(), event.getY()));
        } else if (event.getTarget() == context.getDrawingPane()) {
            Rectangle newRect = new Rectangle(event.getX() - 40, event.getY() - 25, 80, 50);
            newRect.setFill(Color.LIGHTCORAL);
            newRect.setStroke(Color.BLACK);
            newRect.setStrokeWidth(2);

            context.addShapeToModel(newRect);
        }
    }

    @Override
    public void handleMouseDragged(MouseEvent event, StateContext context) {
        // Im Idle passiert beim Ziehen auf leerem Raum nichts
    }

    @Override
    public void handleMouseReleased(MouseEvent event, StateContext context) {
        // Nichts zu tun
    }
}
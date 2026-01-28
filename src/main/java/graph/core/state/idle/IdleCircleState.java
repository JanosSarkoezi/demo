package graph.core.state.idle;

import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.state.active.MoveState;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class IdleCircleState implements EditorState {

    @Override
    public void handleMousePressed(MouseEvent event, StateContext context) {
        if (event.getTarget() instanceof Shape c) {
            context.setCurrentState(new MoveState(c, event.getX(), event.getY()));
        } else {
            Circle circle = new Circle(event.getX(), event.getY(), 30, Color.DODGERBLUE);
            context.addShapeToModel(circle);
        }
    }

    @Override
    public void handleMouseDragged(MouseEvent event, StateContext context) {
        // Im Idle passiert beim Dragging nichts, außer wir wären schon im MoveState
    }

    @Override
    public void handleMouseReleased(MouseEvent event, StateContext context) {}
}
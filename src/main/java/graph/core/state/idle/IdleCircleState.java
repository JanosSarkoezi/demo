package graph.core.state.idle;

import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.state.active.MoveState;
import graph.core.state.active.PanningState;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class IdleCircleState implements EditorState {

    @Override
    public void handleMousePressed(MouseEvent event, StateContext context) {
        if (event.isSecondaryButtonDown()) {
            context.setCurrentState(new PanningState(event.getSceneX(), event.getSceneY(), this));
            return;
        }

        Point2D mouseInWorld = context.getMouseInWorld(event);

        if (event.getTarget() instanceof Shape c) {
            context.setCurrentState(new MoveState(c, mouseInWorld.getX(), mouseInWorld.getY(), this));
        } else {
            Circle circle = new Circle(mouseInWorld.getX(), mouseInWorld.getY(), 30, Color.DODGERBLUE);
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
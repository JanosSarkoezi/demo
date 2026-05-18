package graph.core.state.idle;

import graph.core.model.FmcObject;
import graph.core.model.FmcType;
import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.state.active.MoveState;
import graph.core.state.active.PanningState;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
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
            FmcObject circle = new FmcObject(FmcType.KREIS, mouseInWorld.getX(), mouseInWorld.getY());
            context.getRegistry().addObject(circle);
        }
    }

    @Override
    public void handleMouseDragged(MouseEvent event, StateContext context) {
        // Im Idle passiert beim Dragging nichts, außer wir wären schon im MoveState
    }

    @Override
    public void handleMouseReleased(MouseEvent event, StateContext context) {}
}
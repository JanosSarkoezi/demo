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

public class IdleRectangleState implements EditorState {

    @Override
    public void handleMousePressed(MouseEvent event, StateContext context) {
        if (event.isSecondaryButtonDown()) {
            context.setCurrentState(new PanningState(event.getSceneX(), event.getSceneY(), this));
            return;
        }

        Point2D mouseInWorld = context.getMouseInWorld(event);

        if (event.getTarget() instanceof Shape rect) {
            context.setCurrentState(new MoveState(rect, mouseInWorld.getX(), mouseInWorld.getY(), this));
        } else if (event.getTarget() == context.getDrawingPane()) {
            FmcObject rectObj = new FmcObject(FmcType.QUADRAT, mouseInWorld.getX(), mouseInWorld.getY());
            context.getCommandHistory().executeCommand(new graph.core.command.AddObjectCommand(context.getRegistry(), rectObj));
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
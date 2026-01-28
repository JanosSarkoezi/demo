package graph.core.state.active;

import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.state.idle.IdleCircleState;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class MoveState implements EditorState {
    private final Node nodeToMove;
    private double lastMouseX;
    private double lastMouseY;
    private final EditorState originState;

    public MoveState(Node node, double startX, double startY, EditorState originState) {
        this.nodeToMove = node;
        this.lastMouseX = startX;
        this.lastMouseY = startY;
        this.originState = originState;
    }

    @Override
    public void handleMouseDragged(MouseEvent event, StateContext context) {
        Point2D mouseInWorld = context.getDrawingPane().getMouseInWorld(event);

        double deltaX = mouseInWorld.getX() - lastMouseX;
        double deltaY = mouseInWorld.getY() - lastMouseY;

        nodeToMove.setLayoutX(nodeToMove.getLayoutX() + deltaX);
        nodeToMove.setLayoutY(nodeToMove.getLayoutY() + deltaY);

        lastMouseX = mouseInWorld.getX();
        lastMouseY = mouseInWorld.getY();
    }

    @Override
    public void handleMouseReleased(MouseEvent event, StateContext context) {
        context.setCurrentState(originState);
    }

    @Override
    public void handleMousePressed(MouseEvent event, StateContext context) {}
}
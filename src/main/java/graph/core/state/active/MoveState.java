package graph.core.state.active;

import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.state.idle.IdleCircleState;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class MoveState implements EditorState {
    private final Node nodeToMove;
    private double lastMouseX;
    private double lastMouseY;

    public MoveState(Node node, double startX, double startY) {
        this.nodeToMove = node;
        this.lastMouseX = startX;
        this.lastMouseY = startY;
    }

    @Override
    public void handleMouseDragged(MouseEvent event, StateContext context) {
        double deltaX = event.getX() - lastMouseX;
        double deltaY = event.getY() - lastMouseY;

        nodeToMove.setLayoutX(nodeToMove.getLayoutX() + deltaX);
        nodeToMove.setLayoutY(nodeToMove.getLayoutY() + deltaY);

        lastMouseX = event.getX();
        lastMouseY = event.getY();
    }

    @Override
    public void handleMouseReleased(MouseEvent event, StateContext context) {
        // Zurück in den Idle-Zustand für Kreise
        context.setCurrentState(new IdleCircleState());
    }

    @Override
    public void handleMousePressed(MouseEvent event, StateContext context) {}
}
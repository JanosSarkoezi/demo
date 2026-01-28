package graph.core.state.active;

import graph.core.state.EditorState;
import graph.core.state.StateContext;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class MoveState implements EditorState {
    private final Node nodeToMove;
    private final EditorState originState;
    private final double gridSize = 40.0; // Pixel-Abstand des Grids

    // Offsets, damit das Shape nicht mit der Mitte zum Mauszeiger springt
    private double mouseOffsetX;
    private double mouseOffsetY;

    public MoveState(Node node, double startWorldX, double startWorldY, EditorState origin) {
        this.nodeToMove = node;
        this.originState = origin;

        // Wir berechnen den Abstand zwischen Maus und der Mitte des Shapes
        Point2D center = getCenter(node);
        this.mouseOffsetX = startWorldX - center.getX();
        this.mouseOffsetY = startWorldY - center.getY();
    }

    @Override
    public void handleMouseDragged(MouseEvent event, StateContext context) {
        Point2D worldMouse = context.getMouseInWorld(event);

        double targetCenterX = worldMouse.getX() - mouseOffsetX;
        double targetCenterY = worldMouse.getY() - mouseOffsetY;

        double finalX, finalY;

        if (context.isSnapToGridEnabled()) {
            // SNAP-LOGIK
            finalX = Math.round(targetCenterX / gridSize) * gridSize;
            finalY = Math.round(targetCenterY / gridSize) * gridSize;
        } else {
            // FLÜSSIGE BEWEGUNG
            finalX = targetCenterX;
            finalY = targetCenterY;
        }

        Point2D initial = getInitialCenter(nodeToMove);
        nodeToMove.setTranslateX(finalX - initial.getX());
        nodeToMove.setTranslateY(finalY - initial.getY());
    }

    @Override
    public void handleMouseReleased(MouseEvent event, StateContext context) {
        context.setCurrentState(originState);
    }

    // Hilfsmethode: Wo liegt die Mitte des Objekts aktuell in der Welt?
    private Point2D getCenter(Node node) {
        Point2D initial = getInitialCenter(node);
        return new Point2D(initial.getX() + node.getTranslateX(), initial.getY() + node.getTranslateY());
    }

    // Hilfsmethode: Wo ist die "statische" Mitte (ohne Translate)?
    private Point2D getInitialCenter(Node node) {
        if (node instanceof Circle c) {
            return new Point2D(c.getCenterX(), c.getCenterY());
        } else if (node instanceof Rectangle r) {
            return new Point2D(r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2);
        }
        return new Point2D(0, 0);
    }

    @Override public void handleMousePressed(MouseEvent event, StateContext context) {}
}
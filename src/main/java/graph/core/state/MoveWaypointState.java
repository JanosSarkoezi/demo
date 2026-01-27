package graph.core.state;

import graph.controller.MainController;
import graph.model.WaypointModel;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public record MoveWaypointState(
        WaypointModel waypoint,
        double anchorX,
        double anchorY,
        MainController main
) implements InteractionState {

    /**
     * Bequemer Konstruktor für den Start im IdleState.
     * Berechnet den Anker-Versatz zwischen Maus und Wegpunkt.
     */
    public MoveWaypointState(WaypointModel waypoint, MouseEvent event, MainController main) {
        this(
                waypoint,
                waypoint.getX() - getMouseInWorld(event, main).getX(),
                waypoint.getY() - getMouseInWorld(event, main).getY(),
                main
        );
    }

    @Override
    public InteractionState handleMousePressed(MouseEvent event, Pane canvas) {
        return this; // Bereits initialisiert
    }

    @Override
    public InteractionState handleMouseDragged(MouseEvent event, Pane canvas) {
        Point2D mouseInWorld = getMouseInWorld(event, main);

        double targetX = mouseInWorld.getX() + anchorX;
        double targetY = mouseInWorld.getY() + anchorY;

        // Snap-to-Grid Logik (zentral aus der Toolbar gesteuert)
        if (main.getToolbar().isStickyActive()) {
            double gridSize = 40.0;
            targetX = Math.round(targetX / gridSize) * gridSize;
            targetY = Math.round(targetY / gridSize) * gridSize;
        }

        waypoint.setX(targetX);
        waypoint.setY(targetY);

        return this;
    }

    @Override
    public InteractionState handleMouseReleased(MouseEvent event, Pane canvas) {
        // Hier nutzen wir wieder die zentrale Logik für den Rücksprung
        return getNextBaseState(main);
    }

    /**
     * Hilfsmethode zur Koordinatenumrechnung
     */
    private static Point2D getMouseInWorld(MouseEvent event, MainController main) {
        return main.getCanvas().getView().getShapeLayer()
                .sceneToLocal(event.getSceneX(), event.getSceneY());
    }
}
package graph.core.state;

import graph.controller.MainController;
import graph.model.GraphNode;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public record MoveState(GraphNode model, double anchorX, double anchorY, MainController main)
        implements InteractionState {

    @Override
    public InteractionState handleMousePressed(MouseEvent event, Pane canvas) {
        return this;
    }

    @Override
    public InteractionState handleMouseDragged(MouseEvent event, Pane canvas) {
        Point2D mouseInWorld = main.getCanvas().getView().getWorld().sceneToLocal(event.getSceneX(), event.getSceneY());

        double targetX = mouseInWorld.getX() + anchorX;
        double targetY = mouseInWorld.getY() + anchorY;

        if (main.getToolbar().isStickyActive()) {
            targetX = Math.round(targetX / 40.0) * 40.0;
            targetY = Math.round(targetY / 40.0) * 40.0;
        }

        model.setCenter(targetX, targetY);
        return this; // Wir bleiben im MoveState
    }

    @Override
    public InteractionState handleMouseReleased(MouseEvent event, Pane canvas) {
        // Nutzt die default-Logik aus dem Interface
        return getNextBaseState(main);
    }
}
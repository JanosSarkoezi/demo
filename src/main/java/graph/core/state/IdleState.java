package graph.core.state;

import graph.controller.MainController;
import graph.model.GraphNode;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public record IdleState(MainController main) implements InteractionState {

    @Override
    public InteractionState handleMousePressed(MouseEvent event, Pane canvas) {
        Node hit = event.getPickResult().getIntersectedNode();
        GraphNode model = findModel(hit);

        if (model != null) {
            if (event.isAltDown()) {
                main.getSelectionModel().select(model);
                return this; // Bleibe Idle, nur Selektion geändert
            }

            Point2D mouseInWorld = main.getCanvas().getView().getWorld()
                    .sceneToLocal(event.getSceneX(), event.getSceneY());
            Point2D currentCenter = model.getCenter();

            double anchorX = currentCenter.getX() - mouseInWorld.getX();
            double anchorY = currentCenter.getY() - mouseInWorld.getY();

            return new MoveState(model, anchorX, anchorY, main);
        } else {
            main.getSelectionModel().clear();

            return new PanningState(
                    event.getX(), event.getY(),
                    main.getCanvas().getView().getWorld().getTranslateX(),
                    main.getCanvas().getView().getWorld().getTranslateY(),
                    main
            );
        }
    }

    @Override
    public InteractionState handleMouseDragged(MouseEvent event, Pane canvas) {
        return this;
    }

    @Override
    public InteractionState handleMouseReleased(MouseEvent event, Pane canvas) {
        return this;
    }
}
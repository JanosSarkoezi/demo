package graph.state;

import graph.controller.MainController;
import graph.model.GraphNode;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MoveState implements InteractionState {
    private final MainController main;
    private final GraphNode model;
    private double anchorX, anchorY;

    public MoveState(GraphNode model, MainController main) {
        this.model = model;
        this.main = main;
    }

    @Override
    public void handleMousePressed(MouseEvent event, Pane canvas) {
        // Umrechnung in Welt-Koordinaten
        Point2D mouseInWorld = main.getCanvas().getWorld().sceneToLocal(event.getSceneX(), event.getSceneY());
        Point2D currentCenter = model.getCenter();
        main.getStatusLabel().setText(currentCenter.toString());

        // Anker speichern: Abstand von Maus zur Mitte des Objekts
        anchorX = currentCenter.getX() - mouseInWorld.getX();
        anchorY = currentCenter.getY() - mouseInWorld.getY();
        event.consume();
    }

    @Override
    public void handleMouseDragged(MouseEvent event, Pane canvas) {
        Point2D mouseInWorld = main.getCanvas().getWorld().sceneToLocal(event.getSceneX(), event.getSceneY());

        // Ziel-Zentrum berechnen
        double targetCenterX = mouseInWorld.getX() + anchorX;
        double targetCenterY = mouseInWorld.getY() + anchorY;

        if (main.getToolbar().isStickyActive()) {
            double gridSize = 40.0;
            targetCenterX = Math.round(targetCenterX / gridSize) * gridSize;
            targetCenterY = Math.round(targetCenterY / gridSize) * gridSize;
        }

        // Das Modell weiß selbst, wie es x/y setzen muss, um dieses Zentrum zu erreichen
        model.setCenter(targetCenterX, targetCenterY);
        main.getStatusLabel().setText(model.getCenter().toString());
        event.consume();
    }

    @Override
    public void handleMouseReleased(MouseEvent event, Pane canvas) {
        String selectedTool = main.getToolbar().getSelectedTool();

        if (!selectedTool.equals("NONE")) {
            main.getCanvas().setCurrentState(new CreateNodeState(main));
        } else {
            main.getCanvas().setCurrentState(new IdleState(main));
        }
        event.consume();
    }
}
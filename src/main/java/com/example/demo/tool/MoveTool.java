package com.example.demo.tool;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class MoveTool implements Tool {
    private javafx.scene.Node target = null;
    private Point2D lastMouseInWorld = null;

    @Override
    public String getName() {
        return "MOVE & PAN (Objekt oder Welt)";
    }

    @Override
    public void handle(MouseEvent event, Pane canvas, Group world) {
        // Wir rechnen die aktuelle Mausposition IMMER in Welt-Koordinaten um
        Point2D currentMouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            lastMouseInWorld = currentMouseInWorld;

            if (event.getTarget() instanceof Shape s) {
                target = s;
            } else {
                target = world;
            }
            event.consume();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && target != null) {
            // Delta im Welt-Koordinatensystem (Zoom/Pan bereits "herausgerechnet")
            double deltaX = currentMouseInWorld.getX() - lastMouseInWorld.getX();
            double deltaY = currentMouseInWorld.getY() - lastMouseInWorld.getY();

            target.setTranslateX(target.getTranslateX() + deltaX);
            target.setTranslateY(target.getTranslateY() + deltaY);

            // WICHTIG: Nach der Bewegung die Position neu berechnen
            lastMouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            target = null;
        }
    }
}
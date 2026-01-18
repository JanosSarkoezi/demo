package com.example.demo.tool;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DrawTool implements Tool {
    @Override
    public String getName() {
        return "ZEICHNEN (Klick auf Hintergrund)";
    }

    @Override
    public void handle(MouseEvent event, Pane canvas, Group world) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED && event.getTarget() == canvas) {
            // Umrechnung: Wo ist der Klick relativ zur skalierten/verschobenen Welt?
            Point2D pos = world.sceneToLocal(event.getSceneX(), event.getSceneY());
            Circle c = new Circle(pos.getX(), pos.getY(), 20, Color.CYAN);
            c.setStroke(Color.WHITE);
            world.getChildren().add(c);
        }
    }
}
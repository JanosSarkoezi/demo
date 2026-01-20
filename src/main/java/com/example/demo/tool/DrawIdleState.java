package com.example.demo.tool;

import com.example.demo.controller.ToolbarController;
import com.example.demo.ui.CircleAdapter;
import com.example.demo.ui.RectangleAdapter;
import com.example.demo.ui.ShapeAdapter;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class DrawIdleState implements DrawState {
    @Override
    public void onMousePressed(MouseEvent e, DrawTool tool, Pane c, Group w) {
        // Optional: Hier könnte man eine Vorschau-Form starten
    }

    @Override
    public void onMouseDragged(MouseEvent e, DrawTool tool, Pane c, Group w) {
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawTool tool, Pane canvas, Group world) {
        if (event.getTarget() != canvas) {
            return;
        }

        Point2D pos = world.sceneToLocal(event.getSceneX(), event.getSceneY());
        Shape shape;
        ShapeAdapter adapter;

        if (tool.getToolType() == ToolbarController.ToolType.CIRCLE) {
            Circle c = new Circle(pos.getX(), pos.getY(), 20);
            adapter = new CircleAdapter(c);
            shape = c;
        } else if (tool.getToolType() == ToolbarController.ToolType.RECTANGLE) {
            Rectangle r = new Rectangle(pos.getX() - 20, pos.getY() - 20, 40, 40);
            RectangleAdapter ra = new RectangleAdapter(r);
            adapter = ra;
            shape = r;

            // NEU: Das Label zur Welt hinzufügen, damit es sichtbar wird
            world.getChildren().add(ra.getLabel());
            ra.updateLabelPosition();
        } else {
            return; // Unbekannter ToolType
        }

        // Gemeinsame Konfiguration für alle Shapes
        shape.setUserData(adapter);
        shape.setStroke(Color.BLACK);
        shape.setStrokeWidth(3);
        shape.setFill(Color.TRANSPARENT);

        world.getChildren().add(shape);
        event.consume();
        
    }
}
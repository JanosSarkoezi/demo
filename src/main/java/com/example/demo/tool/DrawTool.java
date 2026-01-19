package com.example.demo.tool;

import com.example.demo.controller.ToolbarController.ToolType; // Enum importieren
import com.example.demo.model.SelectionModel;
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

public class DrawTool implements Tool {
    private final ToolType toolType;
    private final SelectionModel selectionModel;

    public DrawTool(ToolType toolType, SelectionModel selectionModel) {
        this.toolType = toolType;
        this.selectionModel = selectionModel;
    }

    @Override
    public String getName() {
        return "ZEICHNEN: " + toolType;
    }

    @Override
    public void onMouseReleased(MouseEvent event, Pane canvas, Group world) {
        if (event.getTarget() == canvas) {
            Point2D pos = world.sceneToLocal(event.getSceneX(), event.getSceneY());

            Shape shape;
            ShapeAdapter adapter;

            // Logik basierend auf dem ToolType
            if (toolType == ToolType.CIRCLE) {
                Circle c = new Circle(pos.getX(), pos.getY(), 20);
                c.setFill(Color.TRANSPARENT);
                adapter = new CircleAdapter(c);
                shape = c;
            } else if (toolType == ToolType.RECTANGLE) {
                // Erstellt ein 40x40 Rechteck, zentriert auf den Klick
                Rectangle r = new Rectangle(pos.getX() - 20, pos.getY() - 20, 40, 40);
                r.setFill(Color.TRANSPARENT);
                adapter = new RectangleAdapter(r);
                shape = r;
            } else {
                return;
            }

            // selectionModel.setSelectedAdapter(adapter);
            shape.setUserData(adapter); // Adapter für spätere Selektion/Resize speichern
            shape.setStroke(Color.BLACK);
            shape.setStrokeWidth(3);

            world.getChildren().add(shape);
        }
    }
}
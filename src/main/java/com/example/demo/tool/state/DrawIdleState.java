package com.example.demo.tool.state;

import com.example.demo.controller.ToolbarController;
import com.example.demo.diagram.shape.CircleAdapter;
import com.example.demo.diagram.shape.RectangleAdapter;
import com.example.demo.diagram.shape.ShapeAdapter;
import com.example.demo.model.CreateShapeCommand;
import com.example.demo.tool.DrawTool;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

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
        if (event.getTarget() != canvas) return;

        Point2D pos = world.sceneToLocal(event.getSceneX(), event.getSceneY());
        ShapeAdapter adapter;

        // 1. Adapter erstellen (Logik wie vorher, aber ohne direktes world.getChildren().add)
        if (tool.getToolType() == ToolbarController.ToolType.RECTANGLE) {
            Rectangle r = new Rectangle(pos.getX(), pos.getY(), 80, 80);
            adapter = new RectangleAdapter(r);
            setupShapeAppearance(r, adapter);
        } else if (tool.getToolType() == ToolbarController.ToolType.CIRCLE) {
            Circle c = new Circle(pos.getX(), pos.getY(), 20);
            adapter = new CircleAdapter(c);
            setupShapeAppearance(c, adapter);
        } else return;

        // 2. DAS COMMAND FEUERN
        CreateShapeCommand createCmd = new CreateShapeCommand(adapter, world);
        tool.getSelectionModel().getHistory().executeCommand(createCmd);

        // 3. Optional: Das neue Shape direkt auswählen
        tool.getSelectionModel().setSelectedAdapter(adapter);

        event.consume();
    }

    private void setupShapeAppearance(Shape shape, ShapeAdapter adapter) {
        shape.setUserData(adapter);
        shape.setStroke(Color.BLACK);
        shape.setStrokeWidth(3);
        shape.setStrokeType(StrokeType.INSIDE);
        shape.setFill(Color.WHITE);
    }
}
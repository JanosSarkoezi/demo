package com.example.demo.model;

import com.example.demo.diagram.shape.RectangleAdapter;
import com.example.demo.diagram.shape.ShapeAdapter;
import javafx.scene.Group;

public class CreateShapeCommand implements Command {
    private final ShapeAdapter adapter;
    private final Group world;

    public CreateShapeCommand(ShapeAdapter adapter, Group world) {
        this.adapter = adapter;
        this.world = world;
    }

    @Override
    public void execute() {
        // Shape hinzufügen
        if (!world.getChildren().contains(adapter.getShape())) {
            world.getChildren().add(adapter.getShape());
        }

        // Falls es ein Rechteck ist, auch die TextArea hinzufügen
        if (adapter instanceof RectangleAdapter ra) {
            if (!world.getChildren().contains(ra.getTextArea())) {
                world.getChildren().add(ra.getTextArea());
            }
        }
    }

    @Override
    public void undo() {
        // Alles wieder entfernen
        world.getChildren().remove(adapter.getShape());

        if (adapter instanceof RectangleAdapter ra) {
            world.getChildren().remove(ra.getTextArea());
        }
    }
}
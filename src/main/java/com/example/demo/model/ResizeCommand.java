package com.example.demo.model;

import com.example.demo.diagram.shape.ShapeAdapter;
import javafx.geometry.Point2D;

public class ResizeCommand implements Command {
    private final ShapeAdapter adapter;

    // Alter Zustand
    private final Point2D oldPos;
    private final double oldWidth;
    private final double oldHeight;

    // Neuer Zustand
    private final Point2D newPos;
    private final double newWidth;
    private final double newHeight;

    public ResizeCommand(ShapeAdapter adapter,
                         Point2D oldPos, double oldWidth, double oldHeight,
                         Point2D newPos, double newWidth, double newHeight) {
        this.adapter = adapter;
        this.oldPos = oldPos;
        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;
        this.newPos = newPos;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
    }

    @Override
    public void execute() {
        adapter.setPosition(newPos.getX(), newPos.getY());
        adapter.setWidth(newWidth);
        adapter.setHeight(newHeight);
    }

    @Override
    public void undo() {
        adapter.setPosition(oldPos.getX(), oldPos.getY());
        adapter.setWidth(oldWidth);
        adapter.setHeight(oldHeight);
    }
}
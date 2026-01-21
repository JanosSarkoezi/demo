package com.example.demo.model;

import com.example.demo.diagram.shape.ShapeAdapter;
import javafx.geometry.Point2D;

public class MoveCommand implements Command {
    private final ShapeAdapter adapter;
    private final Point2D oldPos;
    private final Point2D newPos;

    public MoveCommand(ShapeAdapter adapter, Point2D oldPos, Point2D newPos) {
        this.adapter = adapter;
        this.oldPos = oldPos;
        this.newPos = newPos;
    }

    @Override
    public void execute() {
        adapter.setCenter(newPos.getX(), newPos.getY());
    }

    @Override
    public void undo() {
        adapter.setCenter(oldPos.getX(), oldPos.getY());
    }
}
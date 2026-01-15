package com.example.demo.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShapeTransformHelper {
    private final ShapeAdapter adapter;
    private final Group zoomGroup;
    private final List<ResizeHandle> handles = new ArrayList<>();
    private final BooleanProperty snapToGrid = new SimpleBooleanProperty(true);
    private final BooleanProperty sticky = new SimpleBooleanProperty(false);

    private double anchorX, anchorY;
    private boolean handlesVisible = false;

    private static final double GRID_SPACING = 50.0;

    // Im ShapeTransformHelper.java
    private final BooleanProperty snapToGridEnabled = new SimpleBooleanProperty(true);
    private final BooleanProperty stickyEnabled = new SimpleBooleanProperty(false);

    public BooleanProperty snapToGridEnabledProperty() {
        return snapToGridEnabled;
    }

    public BooleanProperty stickyEnabledProperty() {
        return stickyEnabled;
    }

    public ShapeTransformHelper(ShapeAdapter adapter, Group zoomGroup) {
        this.adapter = adapter;
        this.zoomGroup = zoomGroup;
        setupInteractions();
    }

    public BooleanProperty snapToGridProperty() { return snapToGrid; }
    public BooleanProperty stickyProperty() { return sticky; }

    private void setupInteractions() {
        adapter.getShape().addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        adapter.getShape().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);
        adapter.getShape().addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleased);
    }

    private void onMousePressed(MouseEvent e) {
        if (e.isControlDown()) toggleHandles();
        else {
            hideHandles();

            // Mouse-Position relativ zur Parent-Group
            Point2D shapePos = adapter.getPosition(); // Position in Parent-Koordinaten
            anchorX = shapePos.getX() - e.getX();
            anchorY = shapePos.getY() - e.getY();

            adapter.getShape().toFront();
        }
        e.consume();
    }

    private void onMouseDragged(MouseEvent e) {
        Point2D mousePos = new Point2D(e.getX(), e.getY());
        double x = mousePos.getX() + anchorX;
        double y = mousePos.getY() + anchorY;

        adapter.setPosition(x, y);
        applySnap();

        updateHandles();
        e.consume();
    }

    private void applySnap() {
        if (!snapToGridEnabled.get()) return;

        // Aktuelle Mitte des Shapes
        Point2D center = adapter.getCenter();

        // Snap-Mittelpunkt auf Grid
        double snappedX = Math.round(center.getX() / GRID_SPACING) * GRID_SPACING;
        double snappedY = Math.round(center.getY() / GRID_SPACING) * GRID_SPACING;

        // Neue Position über Adapter setzen
        adapter.setCenter(snappedX, snappedY);
    }


    private void onMouseReleased(MouseEvent e) { e.consume(); }

    private void toggleHandles() {
        if (handlesVisible) hideHandles();
        else showHandles();
        handlesVisible = !handlesVisible;
    }

    private void showHandles() {
        Map<String, Cursor> handleMap = adapter.getHandles();
        for (var entry : handleMap.entrySet()) {
            String name = entry.getKey();
            Cursor cursor = entry.getValue();

            ResizeHandle handle = new ResizeHandle(name, cursor, zoomGroup);
            handles.add(handle);
        }
        updateHandles();
    }

    private void updateHandles() {
        for (ResizeHandle handle : handles) {
            Point2D pos = adapter.getHandlePosition(handle.getPosition());
            handle.getNode().setX(pos.getX() - ResizeHandle.HANDLE_SIZE / 2);
            handle.getNode().setY(pos.getY() - ResizeHandle.HANDLE_SIZE / 2);
        }
    }

    private void hideHandles() {
        handles.forEach(h -> zoomGroup.getChildren().remove(h.getNode()));
        handles.clear();
        handlesVisible = false;
    }

    private Point2D clampAndSnap(double x, double y) {
        double finalX = x;
        double finalY = y;

        if (snapToGridEnabled.get()) {
            double centerX = x + adapter.getWidth() / 2;
            double centerY = y + adapter.getHeight() / 2;
            finalX = Math.round(centerX / GRID_SPACING) * GRID_SPACING - adapter.getWidth() / 2;
            finalY = Math.round(centerY / GRID_SPACING) * GRID_SPACING - adapter.getHeight() / 2;
        }

        return new Point2D(finalX, finalY);
    }

}

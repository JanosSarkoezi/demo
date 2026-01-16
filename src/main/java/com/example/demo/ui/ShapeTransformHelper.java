package com.example.demo.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

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
        if (e.isControlDown()) {
            toggleHandles();
        } else {
            hideHandles();

            // Nutze das Zentrum für den Anker, nicht die Position (Ecke)
            Point2D center = adapter.getCenter();
            anchorX = center.getX() - e.getX();
            anchorY = center.getY() - e.getY();

            adapter.getShape().toFront();
        }
        e.consume();
    }

// Im ShapeTransformHelper.java

    private void onMouseDragged(MouseEvent e) {
        // 1. Berechne das theoretische neue Zentrum (roh)
        double rawCenterX = e.getX() + anchorX;
        double rawCenterY = e.getY() + anchorY;

        // 2. Snapping auf das Zentrum anwenden
        double finalCenterX = rawCenterX;
        double finalCenterY = rawCenterY;

        if (snapToGridEnabled.get()) {
            finalCenterX = Math.round(rawCenterX / GRID_SPACING) * GRID_SPACING;
            finalCenterY = Math.round(rawCenterY / GRID_SPACING) * GRID_SPACING;
        }

        // 3. Optional: Clamping (Begrenzung), falls gewünscht
        // Hier müsste man die halbe Breite/Höhe abziehen/addieren für die Grenzen

        // 4. Das Zentrum direkt setzen
        adapter.setCenter(finalCenterX, finalCenterY);

        updateHandles();
        e.consume();
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

            // Event-Handler für das Ziehen der Handles hinzufügen
            handle.getNode().setOnMouseDragged(e -> {
                // Mausposition in lokale Koordinaten der ZoomGroup umrechnen
                Point2D localMouse = zoomGroup.sceneToLocal(new Point2D(e.getSceneX(), e.getSceneY()));

                // Adapter delegiert die Größenänderung
                adapter.resize(name, localMouse);

                // Alle Handles an die neuen Positionen anpassen
                updateHandles();
                e.consume();
            });

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
}

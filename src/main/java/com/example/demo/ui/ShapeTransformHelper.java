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
        if (e.isControlDown()) {
            toggleHandles(); //
        } else if (e.isAltDown()) {
            toggleConnectionPoints();
        } else {
            hideHandles();
            hideConnectionPoints();

            // Nutze das Zentrum für den Anker, nicht die Position (Ecke)
            Point2D center = adapter.getCenter();
            anchorX = center.getX() - e.getX();
            anchorY = center.getY() - e.getY();

            adapter.getShape().toFront();
        }
        e.consume();
    }

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
        updateConnectionPoints();
        e.consume();
    }

    private void onMouseReleased(MouseEvent e) { e.consume(); }

    private void toggleHandles() {
        if (handlesVisible) {
            hideHandles();
        } else {
            showHandles();
        }
    }

    private void showHandles() {
        for (String name : adapter.getHandleNames()) {
            Cursor cursor = adapter.getHandleCursor(name);
            ResizeHandle handle = new ResizeHandle(name, cursor, zoomGroup);

            handle.getNode().setOnMouseDragged(e -> {
                Point2D localMouse = zoomGroup.sceneToLocal(e.getSceneX(), e.getSceneY());
                adapter.resize(name, localMouse);
                updateHandles();

                e.consume();
            });

            handles.add(handle);
        }
        updateHandles();
        handlesVisible = true;
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

    // Neue Felder hinzufügen
    private final List<ConnectionDot> connectionDots = new ArrayList<>();
    private boolean connectionPointsVisible = false;

    private void toggleConnectionPoints() {
        if (connectionPointsVisible) {
            hideConnectionPoints();
        } else {
            showConnectionPoints();
        }
    }

    private OrthogonalArrow activeArrow;

    private void showConnectionPoints() {
        for (String name : adapter.getConnectionPointNames()) {
            ConnectionDot dot = new ConnectionDot(name, zoomGroup);

            // --- HIER kommt die Logik rein ---

            dot.getNode().setOnMousePressed(e -> {
                // 1. Startpunkt berechnen
                Point2D startPos = adapter.getConnectionPointPosition(name);
                // 2. Temporären Pfeil erstellen und zur Anzeige hinzufügen
                activeArrow = new OrthogonalArrow(startPos);
                zoomGroup.getChildren().add(activeArrow);
                e.consume();
            });

            dot.getNode().setOnMouseDragged(e -> {
                // 3. Mausposition in Diagramm-Koordinaten umrechnen
                Point2D currentMouse = zoomGroup.sceneToLocal(e.getSceneX(), e.getSceneY());
                Point2D startPos = adapter.getConnectionPointPosition(name);
                // 4. Pfad des Pfeils live aktualisieren
                activeArrow.updatePath(startPos, currentMouse);
                e.consume();
            });

            dot.getNode().setOnMouseReleased(e -> {
                // 5. Ziel finden und Verbindung permanent speichern (Logik folgt im Manager)
                handleArrowRelease(e, dot);
                e.consume();
            });

            connectionDots.add(dot);
        }
        updateConnectionPoints();
        connectionPointsVisible = true;
    }

    private void updateConnectionPoints() {
        for (ConnectionDot dot : connectionDots) {
            Point2D pos = adapter.getConnectionPointPosition(dot.getPointName());
            dot.getNode().setCenterX(pos.getX());
            dot.getNode().setCenterY(pos.getY());
        }
    }

    private void hideConnectionPoints() {
        connectionDots.forEach(d -> zoomGroup.getChildren().remove(d.getNode()));
        connectionDots.clear();
        connectionPointsVisible = false;
    }
}

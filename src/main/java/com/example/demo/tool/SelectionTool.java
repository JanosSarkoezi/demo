package com.example.demo.tool;

import com.example.demo.model.SelectionModel;
import com.example.demo.ui.ResizeHandle;
import com.example.demo.ui.ShapeAdapter;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import java.util.ArrayList;
import java.util.List;

public class SelectionTool implements Tool {
    private Node target = null;
    private ShapeAdapter currentAdapter = null;
    private String activeHandleName = null;

    private double anchorX;
    private double anchorY;
    private final double gridSize = 40.0;

    private final List<ResizeHandle> handles = new ArrayList<>();
    private final Group handleLayer = new Group();
    private final SelectionModel selectionModel;

    public SelectionTool(SelectionModel selectionModel) {
        this.selectionModel = selectionModel;

        // Reagiert auf externe Änderungen am Modell
        this.selectionModel.selectedAdapterProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                handleLayer.getChildren().clear();
                currentAdapter = null;
            } else {
                currentAdapter = newV;
            }
        });
    }

    @Override
    public String getName() { return "SELECTION (Move, Resize & Pan)"; }

    @Override
    public void onActivate(Pane canvas, Group world) {
        // Falls beim Tool-Wechsel schon was selektiert ist, Handles zeigen
        this.currentAdapter = selectionModel.getSelectedAdapter();
        if (currentAdapter != null) {
            showHandles(world);
        }
    }

    @Override
    public void onDeactivate(Pane canvas, Group world) {
        clearHandles(world);
        canvas.setCursor(Cursor.DEFAULT);
    }

    @Override
    public void onMousePressed(MouseEvent event, Pane canvas, Group world) {
        Point2D mouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());

        // 1. Klick auf ein Handle?
        if (event.getTarget() instanceof Rectangle r && r.getUserData() instanceof String handlePos) {
            activeHandleName = handlePos;
            event.consume();
            return;
        }

        // 2. Klick auf ein Shape?
        if (event.getTarget() instanceof Shape s && s.getUserData() instanceof ShapeAdapter adapter) {
            target = s;
            currentAdapter = adapter;

            // Anker für das Verschieben (Zentrumsbasiert)
            Point2D center = currentAdapter.getCenter();
            anchorX = center.getX() - mouseInWorld.getX();
            anchorY = center.getY() - mouseInWorld.getY();

            showHandles(world);
            target.setCursor(Cursor.CLOSED_HAND);
            target.toFront();
            handleLayer.toFront();
        } else {
            // 3. Klick auf Hintergrund (Panning)
            currentAdapter = null;
            clearHandles(world);
            target = world; // Wir setzen das Ziel auf die Welt für Panning

            // Für das Panning speichern wir den Klick-Punkt in Scene-Koordinaten
            anchorX = event.getSceneX() - world.getTranslateX();
            anchorY = event.getSceneY() - world.getTranslateY();
            canvas.setCursor(Cursor.CLOSED_HAND);
        }

        selectionModel.setSelectedAdapter(currentAdapter);
        event.consume();
    }

    @Override
    public void onMouseDragged(MouseEvent event, Pane canvas, Group world) {
        Point2D mouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());

        if (activeHandleName != null && currentAdapter != null) {
            // CASE: RESIZING
            currentAdapter.resize(activeHandleName, mouseInWorld);
            updateHandlePositions();
        } else if (target == world) {
            // CASE: PANNING (Welt verschieben)
            world.setTranslateX(event.getSceneX() - anchorX);
            world.setTranslateY(event.getSceneY() - anchorY);
        } else if (currentAdapter != null) {
            // CASE: MOVING (Shape verschieben mit Snapping)
            double rawX = mouseInWorld.getX() + anchorX;
            double rawY = mouseInWorld.getY() + anchorY;

            currentAdapter.setCenter(
                    Math.round(rawX / gridSize) * gridSize,
                    Math.round(rawY / gridSize) * gridSize
            );
            updateHandlePositions();
        }
    }

    @Override
    public void onMouseReleased(MouseEvent event, Pane canvas, Group world) {
        activeHandleName = null;
        target = null;
        canvas.setCursor(Cursor.DEFAULT);
    }

    // --- Hilfsmethoden für Handles ---

    private void showHandles(Group world) {
        clearHandles(world);
        if (currentAdapter == null) return;

        for (String name : currentAdapter.getHandleNames()) {
            ResizeHandle h = new ResizeHandle(name, currentAdapter.getHandleCursor(name), handleLayer);
            handles.add(h);
        }
        updateHandlePositions();
        if (!world.getChildren().contains(handleLayer)) {
            world.getChildren().add(handleLayer);
        }
    }

    private void updateHandlePositions() {
        if (currentAdapter == null) return;
        for (ResizeHandle h : handles) {
            Point2D pos = currentAdapter.getHandlePosition(h.getPosition());
            h.getNode().setX(pos.getX() - ResizeHandle.HANDLE_SIZE / 2);
            h.getNode().setY(pos.getY() - ResizeHandle.HANDLE_SIZE / 2);
        }
    }

    private void clearHandles(Group world) {
        handleLayer.getChildren().clear();
        handles.clear();
        world.getChildren().remove(handleLayer);
    }
}
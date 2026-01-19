package com.example.demo.tool;

import com.example.demo.model.SelectionModel;
import com.example.demo.ui.ConnectionDot;
import com.example.demo.ui.ResizeHandle;
import com.example.demo.ui.ShapeAdapter;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.HashMap;
import java.util.Map;

public class SelectionTool implements Tool {
    private final SelectionModel selectionModel;
    private Node target = null;
    private ShapeAdapter currentAdapter = null;
    private String activeHandleName = null;

    private double anchorX;
    private double anchorY;
    private final double gridSize = 40.0;

    private final Map<String, Rectangle> handleMap = new HashMap<>();
    private final Map<String, Circle> connectionMap = new HashMap<>();
    private final Group handleLayer = new Group();
    private final Group connectionLayer = new Group();

    public SelectionTool(SelectionModel selectionModel) {
        this.selectionModel = selectionModel;

        // Reaktiv: Wenn das Modell geleert wird, verschwinden die Handles
        this.selectionModel.selectedAdapterProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) clearHandlesFromUI();
        });
    }

    @Override
    public void onActivate(Pane canvas, Group world) {
        this.currentAdapter = selectionModel.getSelectedAdapter();
        if (currentAdapter != null) showHandles(world);
    }

    @Override
    public void onDeactivate(Pane canvas, Group world) {
        clearHandlesFromUI();
        world.getChildren().remove(handleLayer);
        canvas.setCursor(Cursor.DEFAULT);
        selectionModel.setSelectedAdapter(null);
    }

    @Override
    public void onMousePressed(MouseEvent event, Pane canvas, Group world) {
        Point2D mouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());

        // 1. Check: Klick auf Resize-Handle?
        if (event.getTarget() instanceof javafx.scene.shape.Rectangle r && r.getUserData() instanceof String handleName) {
            activeHandleName = handleName;
            event.consume();
            return;
        }

        // 2. Check: Klick auf ein Shape?
        if (event.getTarget() instanceof Shape s && s.getUserData() instanceof ShapeAdapter adapter) {
            handleShapeClick(event, s, adapter, mouseInWorld, world);
            setStatus(1, mouseInWorld);
            canvas.setCursor(Cursor.CLOSED_HAND);
            event.consume();
            return;
        }

        // 3. Fallback: Panning (Klick auf Hintergrund)
        handlePanningStart(event, canvas, world);
        event.consume();
    }

    private void setStatus(Integer number, Point2D mouseInWorld) {
        selectionModel.setStatusMessage(String.format(
                "%s. Tool: %s | Welt-Pos: %.0f, %.0f | Target: %s",
                number, getName(), mouseInWorld.getX(), mouseInWorld.getY(),
                (target != null ? target.getClass().getSimpleName() : "None")
        ));
    }

    private void handleShapeClick(MouseEvent event, Shape shape, ShapeAdapter adapter, Point2D mouseInWorld, Group world) {
        this.target = shape;
        this.currentAdapter = adapter;
        selectionModel.setSelectedAdapter(adapter);

        // IMMER: Anker berechnen, damit onMouseDragged korrekte Werte hat
        Point2D center = adapter.getCenter();
        anchorX = center.getX() - mouseInWorld.getX();
        anchorY = center.getY() - mouseInWorld.getY();

        if (event.isControlDown()) {
            clearHandlesFromUI();
            showConnectionPoints(world);
        } else if (event.isAltDown()) {
            clearConnectionsFromUI();
            showHandles(world);
        } else {
            clearHandlesFromUI();
            clearConnectionsFromUI();
        }

        shape.toFront();
        handleLayer.toFront();
        connectionLayer.toFront();
    }

    private void handlePanningStart(MouseEvent event, Pane canvas, Group world) {
        this.currentAdapter = null;
        this.target = world;
        selectionModel.clear();
        clearHandlesFromUI();
        clearConnectionsFromUI();

        anchorX = event.getSceneX() - world.getTranslateX();
        anchorY = event.getSceneY() - world.getTranslateY();
        canvas.setCursor(Cursor.CLOSED_HAND);
    }

    @Override
    public void onMouseDragged(MouseEvent event, Pane canvas, Group world) {
        Point2D mouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());

        setStatus(6, mouseInWorld);

        if (activeHandleName != null && currentAdapter != null) {
            currentAdapter.resize(activeHandleName, mouseInWorld);
            updateHandlePositions();
            updateConnectionPointPositions();
        } else if (target == world) {
            world.setTranslateX(event.getSceneX() - anchorX);
            world.setTranslateY(event.getSceneY() - anchorY);
        } else if (currentAdapter != null) {
            double rawX = mouseInWorld.getX() + anchorX;
            double rawY = mouseInWorld.getY() + anchorY;
            currentAdapter.setCenter(
                    Math.round(rawX / gridSize) * gridSize,
                    Math.round(rawY / gridSize) * gridSize
            );
            updateHandlePositions();
            updateConnectionPointPositions();
        }
    }

    @Override
    public void onMouseReleased(MouseEvent event, Pane canvas, Group world) {
        Point2D mouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());
        setStatus(7, mouseInWorld);

        activeHandleName = null;
        target = null;
        canvas.setCursor(Cursor.DEFAULT);
    }

    private void showHandles(Group world) {
        clearHandlesFromUI();
        if (currentAdapter == null) return;

        for (String name : currentAdapter.getHandleNames()) {
            ResizeHandle rh = new ResizeHandle(name, currentAdapter.getHandleCursor(name), handleLayer);
            handleMap.put(name, rh.getNode());
        }
        updateHandlePositions();
        if (!world.getChildren().contains(handleLayer)) world.getChildren().add(handleLayer);
    }

    private void updateHandlePositions() {
        if (currentAdapter == null || handleMap.isEmpty()) return;

        // Symmetrie: Wir nutzen exakt dieselben Namen wie beim Erstellen
        for (String name : currentAdapter.getHandleNames()) {
            Rectangle rect = handleMap.get(name);
            if (rect != null) {
                Point2D pos = currentAdapter.getHandlePosition(name);
                rect.setX(pos.getX() - ResizeHandle.HANDLE_SIZE / 2);
                rect.setY(pos.getY() - ResizeHandle.HANDLE_SIZE / 2);
            }
        }
    }

    private void showConnectionPoints(Group world) {
        clearConnectionsFromUI();
        if (currentAdapter == null) return;

        // Wir holen die Punkte vom Adapter (z.B. N, S, E, W)
        for (String name : currentAdapter.getConnectionPointNames()) {
            ConnectionDot dot = new ConnectionDot(name, connectionLayer);
            connectionMap.put(name, dot.getNode());
        }

        updateConnectionPointPositions();

        if (!world.getChildren().contains(connectionLayer)) {
            world.getChildren().add(connectionLayer);
        }
    }

    private void updateConnectionPointPositions() {
        if (currentAdapter == null || connectionMap.isEmpty()) return;

        for (String name : currentAdapter.getConnectionPointNames()) {
            Circle dotCircle = connectionMap.get(name);
            if (dotCircle != null) {
                Point2D pos = currentAdapter.getConnectionPointPosition(name);
                dotCircle.setCenterX(pos.getX());
                dotCircle.setCenterY(pos.getY());
            }
        }
    }

    private void clearConnectionsFromUI() {
        connectionLayer.getChildren().clear();
    }

    private void clearHandlesFromUI() {
        handleLayer.getChildren().clear();
    }

    @Override
    public String getName() { return "Selection Tool"; }
}
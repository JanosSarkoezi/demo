package com.example.demo.controller;

import com.example.demo.model.SelectionModel;
import com.example.demo.tool.SelectionTool;
import com.example.demo.tool.Tool;
import com.example.demo.ui.CanvasCamera;
import com.example.demo.ui.ConnectionDot;
import com.example.demo.ui.DraggableCircle;
import com.example.demo.ui.DraggableRectangle;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CanvasController {
    @FXML private Pane drawingCanvas;
    private final Group world = new Group();
    private Tool currentTool;
    private CanvasCamera camera;
    private SelectionModel selectionModel;

    // Wir speichern die Properties hier lokal, um sie neuen Shapes zuzuweisen
    private BooleanProperty snapToGridRef;
    private BooleanProperty stickyRef;

    @FXML
    public void initialize() {
        // 1. Hierarchie & Kamera Setup
        drawingCanvas.getChildren().add(world);
        camera = new CanvasCamera(world);

        // 2. Clipping (Damit Inhalte nicht über den Rand ragen)
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(drawingCanvas.widthProperty());
        clip.heightProperty().bind(drawingCanvas.heightProperty());
        drawingCanvas.setClip(clip);

        drawingCanvas.addEventHandler(MouseEvent.ANY, e -> {
            if (currentTool != null) {
                currentTool.handle(e, drawingCanvas, world);
            }
        });

        drawingCanvas.addEventHandler(ScrollEvent.SCROLL, camera::handleZoom);
        drawingCanvas.setOnDragOver(this::handleCanvasDragOver);
        drawingCanvas.setOnDragDropped(this::handleCanvasDragDropped);
    }

    public void injectProperties(BooleanProperty snap, BooleanProperty sticky, SelectionModel model) {
        this.snapToGridRef = snap;
        this.stickyRef = sticky;

        this.selectionModel = model;
        this.currentTool = new SelectionTool(selectionModel);
    }

    private void startToolDrag(StackPane tool, String format, MouseEvent event) {
        Dragboard db = tool.startDragAndDrop(TransferMode.COPY);
        ClipboardContent content = new ClipboardContent();
        content.putString(format);
        db.setContent(content);
        event.consume();
    }

    private void handleCanvasDragOver(DragEvent event) {
        if (event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleCanvasDragDropped(DragEvent event) {
        String toolType = event.getDragboard().getString();
        Point2D p = world.sceneToLocal(event.getSceneX(), event.getSceneY());

        if ("NEW_CIRCLE".equals(toolType)) {
            DraggableCircle circle = new DraggableCircle(p.getX(), p.getY(), 25, Color.DODGERBLUE, drawingCanvas, world);

            // BINDUNG: Der Helper des neuen Kreises hört auf die CheckBox
            circle.getTransformHelper().snapToGridEnabledProperty().bind(snapToGridRef);

            world.getChildren().add(circle);

        } else if ("NEW_RECT".equals(toolType)) {
            DraggableRectangle rect = new DraggableRectangle(p.getX(), p.getY(), 50, 50, Color.RED, drawingCanvas, world);

            // BINDUNG: Der Helper des neuen Rechtecks hört auf die CheckBox
            rect.getTransformHelper().snapToGridEnabledProperty().bind(snapToGridRef);
            rect.getTransformHelper().stickyEnabledProperty().bind(stickyRef);

            world.getChildren().add(rect);
        }

        event.setDropCompleted(true);
        event.consume();
    }

    private void createYellowMovableDot(MouseEvent e) {
        // Punkt erstellen
        ConnectionDot yellowDot = new ConnectionDot("FREE_DOT", world);
        yellowDot.getNode().setFill(Color.YELLOW);
        yellowDot.getNode().setStroke(Color.BLACK);

        // Position relativ zur zoomGroup setzen
        Point2D localPos = world.sceneToLocal(e.getSceneX(), e.getSceneY());
        yellowDot.getNode().setCenterX(localPos.getX());
        yellowDot.getNode().setCenterY(localPos.getY());

        // Bewegungs-Logik mit LINKS hinzufügen
        yellowDot.getNode().setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                Point2D dragPos = world.sceneToLocal(event.getSceneX(), event.getSceneY());
                yellowDot.getNode().setCenterX(dragPos.getX());
                yellowDot.getNode().setCenterY(dragPos.getY());
            }
            event.consume();
        });
    }

    public Tool getCurrentTool() {
        return currentTool;
    }

    public void setCurrentTool(Tool currentTool) {
        this.currentTool = currentTool;
    }
}
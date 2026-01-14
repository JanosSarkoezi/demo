package com.example.demo.controller;

import com.example.demo.ui.CanvasCamera;
import com.example.demo.ui.DraggableCircle;
import com.example.demo.ui.DraggableRectangle;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HelloController {

    @FXML public CheckBox snapToGridCheckbox;
    @FXML private StackPane circleTool;
    @FXML private StackPane rectTool;
    @FXML private Pane drawingCanvas;

    private final Group zoomGroup = new Group();
    private CanvasCamera camera;

    @FXML
    public void initialize() {
        // 1. Hierarchie & Kamera Setup
        drawingCanvas.getChildren().add(zoomGroup);
        camera = new CanvasCamera(drawingCanvas, zoomGroup);

        // 2. Clipping (Damit Inhalte nicht über den Rand ragen)
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(drawingCanvas.widthProperty());
        clip.heightProperty().bind(drawingCanvas.heightProperty());
        drawingCanvas.setClip(clip);

        // 3. Canvas Events (Zoom & Panning)
        drawingCanvas.setOnScroll(camera::handleZoom);

        drawingCanvas.setOnMousePressed(e -> {
            drawingCanvas.setCursor(Cursor.CLOSED_HAND);
            camera.startPan(e);
        });

        drawingCanvas.setOnMouseDragged(camera::updatePan);

        drawingCanvas.setOnMouseReleased(e -> drawingCanvas.setCursor(Cursor.DEFAULT));

        // 4. Drag & Drop Logik (für neue Kreise)
        circleTool.setOnDragDetected(e -> startToolDrag(circleTool, "NEW_CIRCLE", e));
        rectTool.setOnDragDetected(e -> startToolDrag(rectTool, "NEW_RECT", e));
        drawingCanvas.setOnDragOver(this::handleCanvasDragOver);
        drawingCanvas.setOnDragDropped(this::handleCanvasDragDropped);
    }

    // --- Drag & Drop Handler ---

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
        Point2D p = zoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());

        if ("NEW_CIRCLE".equals(toolType)) {
            DraggableCircle circle = new DraggableCircle(p.getX(), p.getY(), 25, Color.DODGERBLUE, drawingCanvas, zoomGroup);

            // BINDUNG: Der Helper des neuen Kreises hört auf die CheckBox
            circle.getTransformHelper().snapToGridEnabledProperty().bind(snapToGridCheckbox.selectedProperty());

            zoomGroup.getChildren().add(circle);

        } else if ("NEW_RECT".equals(toolType)) {
            DraggableRectangle rect = new DraggableRectangle(p.getX(), p.getY(), 50, 50, Color.RED, drawingCanvas, zoomGroup);

            // BINDUNG: Der Helper des neuen Rechtecks hört auf die CheckBox
            rect.getTransformHelper().snapToGridEnabledProperty().bind(snapToGridCheckbox.selectedProperty());

            zoomGroup.getChildren().add(rect);
        }

        event.setDropCompleted(true);
        event.consume();
    }
}
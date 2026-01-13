package com.example.demo;

import com.example.demo.util.CanvasCamera;
import com.example.demo.util.DraggableCircle;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HelloController {

    @FXML private StackPane circleTool;
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
        circleTool.setOnDragDetected(this::handleToolDragDetected);
        drawingCanvas.setOnDragOver(this::handleCanvasDragOver);
        drawingCanvas.setOnDragDropped(this::handleCanvasDragDropped);
    }

    // --- Drag & Drop Handler ---

    private void handleToolDragDetected(MouseEvent event) {
        Dragboard db = circleTool.startDragAndDrop(TransferMode.COPY);
        ClipboardContent content = new ClipboardContent();
        content.putString("NEW_CIRCLE");
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
        if ("NEW_CIRCLE".equals(event.getDragboard().getString())) {
            // Umrechnung der Mausposition in das lokale (skalierte) Koordinatensystem
            Point2D p = zoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());

            // Neuen DraggableCircle erstellen
            DraggableCircle circle = new DraggableCircle(p.getX(), p.getY(), 25, Color.DODGERBLUE, drawingCanvas, zoomGroup);
            zoomGroup.getChildren().add(circle);
        }
        event.setDropCompleted(true);
        event.consume();
    }
}
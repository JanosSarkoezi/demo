package com.example.demo.controller;

import com.example.demo.model.SelectionModel;
import com.example.demo.tool.SelectionTool;
import com.example.demo.tool.Tool;
import com.example.demo.ui.CanvasCamera;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
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

        drawingCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> currentTool.onMousePressed(e, drawingCanvas, world));
        drawingCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> currentTool.onMouseDragged(e, drawingCanvas, world));
        drawingCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> currentTool.onMouseReleased(e, drawingCanvas, world));

        drawingCanvas.addEventHandler(ScrollEvent.SCROLL, camera::handleZoom);
    }

    public void injectProperties(BooleanProperty snap, BooleanProperty sticky, SelectionModel model) {
        this.snapToGridRef = snap;
        this.stickyRef = sticky;

        this.selectionModel = model;
        this.currentTool = new SelectionTool(selectionModel);
    }

    public void setCurrentTool(Tool currentTool) {
        this.currentTool = currentTool;
    }
}
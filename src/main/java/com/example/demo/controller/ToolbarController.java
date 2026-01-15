package com.example.demo.controller;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

public class ToolbarController {
    @FXML public CheckBox snapToGridCheckbox;
    @FXML public CheckBox stickyCheckbox;
    @FXML private StackPane circleTool;
    @FXML private StackPane rectTool;

    @FXML
    public void initialize() {
        // Logik für den Start des Drags (von HelloController hierher verschoben)
        circleTool.setOnDragDetected(e -> startToolDrag(circleTool, "NEW_CIRCLE", e));
        rectTool.setOnDragDetected(e -> startToolDrag(rectTool, "NEW_RECT", e));
    }

    private void startToolDrag(StackPane tool, String format, MouseEvent event) {
        Dragboard db = tool.startDragAndDrop(TransferMode.COPY);
        ClipboardContent content = new ClipboardContent();
        content.putString(format);
        db.setContent(content);
        event.consume();
    }

    // Getter, damit der MainController die Properties an den Canvas binden kann
    public BooleanProperty snapToGridProperty() { return snapToGridCheckbox.selectedProperty(); }
    public BooleanProperty stickyProperty() { return stickyCheckbox.selectedProperty(); }
}
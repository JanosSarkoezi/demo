package com.example.demo.controller;

import com.example.demo.tool.DrawTool;
import com.example.demo.tool.SelectionTool;
import javafx.fxml.FXML;

public class MainController {
    @FXML private ToolbarController toolbarController; // Name muss fx:id + "Controller" sein
    @FXML private CanvasController canvasController;

    @FXML
    public void initialize() {
        // Verbinde die Checkbox-Properties der Toolbar mit dem Canvas
        canvasController.injectProperties(
                toolbarController.snapToGridProperty(),
                toolbarController.stickyProperty()
        );

        toolbarController.selectedToolProperty().addListener((obs, oldTool, newTool) -> {
            if (newTool == ToolbarController.ToolType.CIRCLE || newTool == ToolbarController.ToolType.RECTANGLE) {
                // Setze das aktuelle Tool auf eine neue Instanz von DrawTool
                canvasController.setCurrentTool(new DrawTool(newTool));
            } else {
                canvasController.setCurrentTool(new SelectionTool());
            }
        });
    }
}
package com.example.demo.controller;

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
    }
}
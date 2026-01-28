package graph.controller;

import graph.core.ToolType;
import graph.core.state.EditorState;
import graph.core.state.idle.IdleCircleState;
import graph.core.state.idle.IdleConnectionState;
import graph.core.state.idle.IdleRectangleState;
import graph.model.DrawingModel;
import javafx.fxml.FXML;

public class MainController {
    private final DrawingModel drawingModel = new DrawingModel();

    @FXML private ToolbarController toolbarController; // fx:id="toolbar" in main-view.fxml
    @FXML private CanvasController canvasController;   // fx:id="canvas" in main-view.fxml

    @FXML
    public void initialize() {
        canvasController.setModel(drawingModel);
        toolbarController.setOnToolSelected(this::handleToolSelection);
        toolbarController.setOnSnapChanged(enabled -> canvasController.setSnapEnabled(enabled));
    }

    private void handleToolSelection(ToolType tool) {
        EditorState newState = switch (tool) {
            case CIRCLE     -> new IdleCircleState();
            case RECTANGLE  -> new IdleRectangleState();
            case CONNECTION -> new IdleConnectionState();
        };

        canvasController.setCurrentState(newState);
        System.out.println("Tool gewählt: " + tool);
    }
}
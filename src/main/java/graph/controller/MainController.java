package graph.controller;

import graph.core.registry.NodeRegistry;
import graph.core.state.CreateNodeState;
import graph.core.state.IdleState;
import graph.core.strategy.CircleCreationStrategy;
import graph.core.strategy.RectangleCreationStrategy;
import graph.model.GraphModel;
import graph.model.SelectionModel;
import graph.view.SelectionRenderer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML private Label statusLabel;
    @FXML private ToolbarController toolbarController;
    @FXML private CanvasController canvasController;

    // Neue zentrale Instanzen
    private SelectionModel selectionModel;
    private SelectionRenderer selectionRenderer;

    private final GraphModel graphModel = new GraphModel();

    @FXML
    public void initialize() {
        // 1. Daten-Modell initialisieren
        selectionModel = new SelectionModel();

        if (canvasController != null && toolbarController != null) {
            canvasController.init(this);
            toolbarController.init(this);

            // 2. Renderer initialisieren (verknüpft UI-Layer mit Modell)
            // Wir nutzen den uiLayer vom CanvasController
            selectionRenderer = new SelectionRenderer(
                    canvasController.getView().getUiLayer(),
                    selectionModel,
                    this
            );

            statusLabel.setText("Editor bereit");
        }

        NodeRegistry.register("CIRCLE", new CircleCreationStrategy());
        NodeRegistry.register("RECTANGLE", new RectangleCreationStrategy());
    }

    // Im MainController.java
    public void updateCanvasState() {
        // Wir nutzen deine Methode!
        String tool = toolbarController.getSelectedTool();

        switch (tool) {
            case "CIRCLE", "RECTANGLE" ->
                    canvasController.setCurrentState(new CreateNodeState(this));
//            case "CONNECT" ->
//                    canvasController.setCurrentState(new ConnectionState(this));
            default ->
                    canvasController.setCurrentState(new IdleState(this));
        }
    }

    public GraphModel getGraphModel() {
        return graphModel;
    }

    // Getter, damit States auf die Auswahl zugreifen können
    public SelectionModel getSelectionModel() {
        return selectionModel;
    }

    public ToolbarController getToolbar() { return toolbarController; }
    public CanvasController getCanvas() { return canvasController; }
    public Label getStatusLabel() { return statusLabel; }
}
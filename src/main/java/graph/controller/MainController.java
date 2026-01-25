package graph.controller; // Entsprechend deiner Struktur im Screenshot

import graph.core.registry.NodeRegistry;
import graph.core.strategy.CircleCreationStrategy;
import graph.core.strategy.RectangleCreationStrategy;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML private Label statusLabel;

    @FXML private ToolbarController toolbarController;
    @FXML private CanvasController canvasController;

    @FXML
    public void initialize() {
        if (canvasController != null && toolbarController != null) {
            // Übergabe der Toolbar-Referenz an den Canvas-Controller
            canvasController.init(this);
            toolbarController.init(this);
            statusLabel.setText("Editor bereit");
        }

        NodeRegistry.register("CIRCLE", new CircleCreationStrategy());
        NodeRegistry.register("RECTANGLE", new RectangleCreationStrategy());
    }

    public ToolbarController getToolbar() {
        return toolbarController;
    }

    public CanvasController getCanvas() {
        return canvasController;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }
}
package graph.view;

import graph.controller.MainController;
import graph.core.adapter.ResizeHandle;
import graph.core.adapter.ShapeAdapter;
import graph.core.factory.AdapterFactory;
import graph.core.state.ResizeState;
import graph.model.SelectionModel;
import graph.model.GraphNode;
import javafx.beans.binding.Bindings;
import javafx.scene.Group;

public class SelectionRenderer {
    private final Group uiLayer;
    private final SelectionModel selectionModel;
    private final MainController main; // Noch nötig für State-Wechsel

    public SelectionRenderer(Group uiLayer, SelectionModel selectionModel, MainController main) {
        this.uiLayer = uiLayer;
        this.selectionModel = selectionModel;
        this.main = main;

        // Automatische Reaktion auf Auswahländerungen
        this.selectionModel.selectedNodeProperty().addListener((obs, oldNode, newNode) -> {
            renderSelection(newNode);
        });
    }

    private void renderSelection(GraphNode node) {
        uiLayer.getChildren().clear();
        if (node == null) return;

        ShapeAdapter adapter = AdapterFactory.createAdapter(node);

        for (String name : adapter.getHandleNames()) {
            ResizeHandle handle = new ResizeHandle(name, adapter.getHandleCursor(name), uiLayer);

            // Die Bindings kommen aus deinem bestehenden Code
            handle.getNode().layoutXProperty().bind(Bindings.createDoubleBinding(
                    () -> adapter.getHandlePosition(name).getX() - (ResizeHandle.HANDLE_SIZE / 2),
                    adapter.getHandleDependencies(name)
            ));

            handle.getNode().layoutYProperty().bind(Bindings.createDoubleBinding(
                    () -> adapter.getHandlePosition(name).getY() - (ResizeHandle.HANDLE_SIZE / 2),
                    adapter.getHandleDependencies(name)
            ));

            handle.getNode().setOnMousePressed(e -> {
                main.getCanvas().setCurrentState(new ResizeState(adapter, name, main));
                e.consume();
            });
        }
    }
}
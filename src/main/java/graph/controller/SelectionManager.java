package graph.controller;

import graph.model.GraphNode;
import graph.model.adapter.ResizeHandle;
import graph.model.adapter.ShapeAdapter;
import graph.state.ResizeState;
import graph.state.factory.AdapterFactory;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import java.util.ArrayList;
import java.util.List;

public class SelectionManager {
    private final MainController main;
    private GraphNode selectedModel;
    private ShapeAdapter selectedAdapter;
    private final List<ResizeHandle> currentHandles = new ArrayList<>();

    public SelectionManager(MainController main) {
        this.main = main;
    }

    public void select(GraphNode model) {
        clearSelection(); // Vorherige Auswahl aufräumen
        this.selectedModel = model;
        this.selectedAdapter = AdapterFactory.createAdapter(model);

        showHandles();
    }

    private void showHandles() {
        Group uiLayer = main.getCanvas().getUiLayer();

        for (String name : selectedAdapter.getHandleNames()) {
            ResizeHandle handle = new ResizeHandle(
                    name,
                    selectedAdapter.getHandleCursor(name),
                    uiLayer
            );

            Observable[] dependencies = selectedAdapter.getHandleDependencies(name);

            handle.getNode().layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
                        return selectedAdapter.getHandlePosition(name).getX() - (ResizeHandle.HANDLE_SIZE / 2);
                    },
                    dependencies
            ));

            handle.getNode().layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
                        return selectedAdapter.getHandlePosition(name).getY() - (ResizeHandle.HANDLE_SIZE / 2);
                    },
                    dependencies
            ));

            handle.getNode().setOnMousePressed(e -> {
                main.getCanvas().setCurrentState(new ResizeState(selectedAdapter, name, main));
                e.consume();
            });

            currentHandles.add(handle);
        }
    }

    public void clearSelection() {
        this.selectedModel = null;
        this.selectedAdapter = null;
        currentHandles.clear();
        main.getCanvas().getUiLayer().getChildren().clear(); // UI-Layer leeren
    }

    public ShapeAdapter getSelectedAdapter() { return selectedAdapter; }
    public GraphNode getSelectedModel() { return selectedModel; }
}
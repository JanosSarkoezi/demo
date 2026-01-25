package graph.view;

import graph.controller.MainController;
import graph.core.adapter.ResizeHandle;
import graph.core.adapter.ShapeAdapter;
import graph.core.factory.AdapterFactory;
import graph.core.state.ResizeState;
import graph.model.SelectionModel;
import graph.model.GraphNode;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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

    private void drawPorts(GraphNode node, GraphView view) {
        ShapeAdapter adapter = AdapterFactory.createAdapter(node);

        for (int i = 0; i < adapter.getPortCount(); i++) {
            final int portIndex = i;

            Circle bluePort = new Circle(5, Color.CORNFLOWERBLUE);
            bluePort.setStroke(Color.WHITE);

            bluePort.getProperties().put("node", node);
            bluePort.getProperties().put("portIndex", portIndex);

            Observable[] deps = adapter.getHandleDependencies("ANY");

            bluePort.centerXProperty().bind(Bindings.createDoubleBinding(
                    () -> adapter.getPortPosition(portIndex).getX(), deps));

            bluePort.centerYProperty().bind(Bindings.createDoubleBinding(
                    () -> adapter.getPortPosition(portIndex).getY(), deps));

            view.getUiLayer().getChildren().add(bluePort);
        }
    }
}
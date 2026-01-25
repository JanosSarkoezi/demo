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
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

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

    public void updatePorts(List<GraphNode> allNodes, GraphView view) {
        // 1. Alle blauen Ports entfernen, deren Nodes nicht mehr selektiert sind
        view.getUiLayer().getChildren().removeIf(n -> {
            if (n instanceof Circle && Color.CORNFLOWERBLUE.equals(((Circle) n).getFill())) {
                GraphNode node = (GraphNode) n.getProperties().get("node");
                return !node.isSelected(); // Weg damit, wenn nicht mehr selektiert
            }
            return false;
        });

        // 2. Ports für alle Nodes hinzufügen, die selektiert sind, aber noch keine Ports haben
        for (GraphNode node : allNodes) {
            if (node.isSelected() && !isNodeAlreadyPorted(node, view)) {
                drawPortsForSingleNode(node, view);
            }
        }
    }

    private void drawPortsForSingleNode(GraphNode node, GraphView view) {
        ShapeAdapter adapter = AdapterFactory.createAdapter(node);

        for (int i = 0; i < adapter.getPortCount(); i++) {
            final int portIndex = i;

            Circle bluePort = new Circle(5, Color.CORNFLOWERBLUE);
            bluePort.setStroke(Color.WHITE);

            // Metadaten für den ConnectionState speichern
            bluePort.getProperties().put("node", node);
            bluePort.getProperties().put("portIndex", portIndex);

            // Bindings erstellen, damit Ports bei Bewegung mitwandern
            Observable[] deps = adapter.getHandleDependencies("ANY");
            bluePort.centerXProperty().bind(Bindings.createDoubleBinding(
                    () -> adapter.getPortPosition(portIndex).getX(), deps));
            bluePort.centerYProperty().bind(Bindings.createDoubleBinding(
                    () -> adapter.getPortPosition(portIndex).getY(), deps));

            view.getUiLayer().getChildren().add(bluePort);
        }
    }

    private boolean isNodeAlreadyPorted(GraphNode node, GraphView view) {
        return view.getUiLayer().getChildren().stream()
                .filter(n -> n instanceof Circle && Color.CORNFLOWERBLUE.equals(((Circle) n).getFill()))
                .anyMatch(n -> n.getProperties().get("node") == node);
    }
}
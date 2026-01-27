package graph.view;

import graph.controller.MainController;
import graph.core.adapter.ShapeAdapter;
import graph.core.factory.AdapterFactory;
import graph.model.ConnectionModel;
import graph.model.GraphNode;
import graph.model.WaypointModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.List;

/**
 * Verantwortlich für die Darstellung von Verbindungen, Wegpunkten und Ports.
 */
public class ConnectionRenderer {

    private final MainController main;

    public ConnectionRenderer(MainController main) {
        this.main = main;
    }

    /**
     * Aktualisiert die Sichtbarkeit der Ports basierend auf dem Selektionsstatus der Knoten.
     * Ersetzt die alte Logik aus dem SelectionRenderer.
     */
    public void updatePorts(List<GraphNode> allNodes, GraphView view) {
        // 1. Alle alten Ports entfernen
        hideAllPorts(view);

        // 2. Neue Ports für selektierte Knoten zeichnen
        for (GraphNode node : allNodes) {
            if (node.isSelected()) {
                drawPortsForNode(node, view);
            }
        }
    }

    /**
     * Zeichnet die Ports für einen spezifischen Knoten und bindet sie an dessen Position.
     */
    private void drawPortsForNode(GraphNode node, GraphView view) {
        ShapeAdapter adapter = AdapterFactory.createAdapter(node);

        for (int i = 0; i < adapter.getPortCount(); i++) {
            final int portIndex = i;
            Circle port = new Circle(5, Color.CORNFLOWERBLUE);
            port.setStroke(Color.WHITE);
            port.getStyleClass().add("port");

            // Metadaten für das Picking im State (InitialConnectionState)
            port.getProperties().put("node", node);
            port.getProperties().put("portIndex", portIndex);

            // Bindings: Ports folgen dem Knoten bei Bewegung oder Resize
            Observable[] deps = adapter.getHandleDependencies("ANY");
            port.centerXProperty().bind(Bindings.createDoubleBinding(
                    () -> adapter.getPortPosition(portIndex).getX(), deps));
            port.centerYProperty().bind(Bindings.createDoubleBinding(
                    () -> adapter.getPortPosition(portIndex).getY(), deps));

            view.getUiLayer().getChildren().add(port);
        }
    }

    /**
     * Entfernt alle Port-Grafiken vom UI-Layer.
     */
    public void hideAllPorts(GraphView view) {
        view.getUiLayer().getChildren().removeIf(n -> n.getStyleClass().contains("port"));
    }

    /**
     * Erstellt eine permanente visuelle Verbindung im Shape-Layer.
     */
    public void addConnection(ConnectionModel model, GraphView view) {
        Polyline line = new Polyline();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);

        ShapeAdapter startAdapter = AdapterFactory.createAdapter(model.getStartNode());
        ShapeAdapter endAdapter = AdapterFactory.createAdapter(model.getEndNode());

        Observable[] startDeps = startAdapter.getHandleDependencies("ANY");
        DoubleBinding startX = Bindings.createDoubleBinding(() ->
                startAdapter.getPortPosition(model.getStartPortIndex()).getX(), startDeps);
        DoubleBinding startY = Bindings.createDoubleBinding(() ->
                startAdapter.getPortPosition(model.getStartPortIndex()).getY(), startDeps);

        Observable[] endDeps = endAdapter.getHandleDependencies("ANY");
        DoubleBinding endX = Bindings.createDoubleBinding(() ->
                endAdapter.getPortPosition(model.getEndPortIndex()).getX(), endDeps);
        DoubleBinding endY = Bindings.createDoubleBinding(() ->
                endAdapter.getPortPosition(model.getEndPortIndex()).getY(), endDeps);

        InvalidationListener updateTrigger = obs -> updateLinePoints(line, startX, startY, model.getWaypoints(), endX, endY);

        startX.addListener(updateTrigger);
        startY.addListener(updateTrigger);
        endX.addListener(updateTrigger);
        endY.addListener(updateTrigger);

        updateLinePoints(line, startX, startY, model.getWaypoints(), endX, endY);
        view.getShapeLayer().getChildren().add(line);

        for (WaypointModel wp : model.getWaypoints()) {
            wp.xProperty().addListener(updateTrigger);
            wp.yProperty().addListener(updateTrigger);
            drawWaypointHandle(wp, view);
        }
    }

    private void updateLinePoints(Polyline line, DoubleBinding sx, DoubleBinding sy,
                                  List<WaypointModel> wps, DoubleBinding ex, DoubleBinding ey) {
        line.getPoints().clear();
        line.getPoints().addAll(sx.get(), sy.get());
        for (WaypointModel wp : wps) {
            line.getPoints().addAll(wp.getX(), wp.getY());
        }
        line.getPoints().addAll(ex.get(), ey.get());
    }

    private void drawWaypointHandle(WaypointModel wp, GraphView view) {
        Circle handle = new Circle(8, Color.YELLOW);
        handle.setStroke(Color.ORANGE);
        handle.setOpacity(0.5);

        handle.centerXProperty().bindBidirectional(wp.xProperty());
        handle.centerYProperty().bindBidirectional(wp.yProperty());

        handle.setOnMousePressed(Event::consume);
        handle.setOnMouseDragged(event -> {
            Point2D local = view.getShapeLayer().sceneToLocal(event.getSceneX(), event.getSceneY());
            double targetX = local.getX();
            double targetY = local.getY();

            if (main.getToolbar().isStickyActive()) {
                double gridSize = 40.0;
                targetX = Math.round(targetX / gridSize) * gridSize;
                targetY = Math.round(targetY / gridSize) * gridSize;
            }

            wp.setX(targetX);
            wp.setY(targetY);
            event.consume();
        });

        view.getShapeLayer().getChildren().add(handle);
    }
}
package graph.core.state.idle;

import graph.core.selection.SelectionManager;
import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.state.active.ConnectionState;
import graph.core.state.active.MoveState;
import graph.core.util.Port;
import graph.core.util.PortCalculator;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.List;

public class IdleConnectionState implements EditorState {

    @Override
    public void handleMousePressed(MouseEvent event, StateContext context) {
        SelectionManager sm = context.getSelectionManager();
        Point2D mouseInWorld = context.getMouseInWorld(event);
        Node target = (Node) event.getTarget();

        // 1. Priorität: Klick auf einen gelben Port -> Neue Verbindung starten
        if (target instanceof Circle portCircle && isPort(portCircle)) {
            context.setCurrentState(new ConnectionState(portCircle, mouseInWorld));
            return;
        }

        // 2. Priorität: Klick auf einen Waypoint -> Bestehende Verbindung verformen
        if (target instanceof Circle waypoint && isWaypoint(waypoint)) {
            context.setCurrentState(new MoveState(waypoint, mouseInWorld.getX(), mouseInWorld.getY(), this));
            return;
        }

        // 3. Priorität: Klick auf ein Shape -> Selektion togglen und Ports aktualisieren
        if (target instanceof Shape clickedShape) {
            // Der SelectionManager kümmert sich um das Hinzufügen/Entfernen
            sm.toggleSelection(clickedShape);
            // Danach zeichnen wir die Ports für alle aktuell selektierten Objekte neu
            refreshPorts(context);
        }
        // 4. Priorität: Klick ins Leere -> Alles abwählen
        else if (target == context.getDrawingPane()) {
            sm.clearSelection();
            refreshPorts(context);
        }
    }

    private void refreshPorts(StateContext context) {
        context.getDrawingPane().getUiLayer().getChildren().clear();

        for (Node selectedNode : context.getSelectionManager().getSelectedNodes()) {

            List<Port> ports = PortCalculator.getPortsForNode(selectedNode);
            for (Port p : ports) {
                Circle portCircle = createPortCircle(p, selectedNode);
                context.getDrawingPane().addNode(portCircle);
            }
        }
    }

    private Circle createPortCircle(Port p, Node selectedNode) {
        // 1. Optik: Ein kleiner gelber Kreis
        Circle portCircle = new Circle(6, Color.YELLOW);
        portCircle.setStroke(Color.GOLDENROD);
        portCircle.setStrokeWidth(1.5);

        portCircle.setCenterX(p.position().getX() - selectedNode.getTranslateX());
        portCircle.setCenterY(p.position().getY() - selectedNode.getTranslateY());

        portCircle.translateXProperty().bind(selectedNode.translateXProperty());
        portCircle.translateYProperty().bind(selectedNode.translateYProperty());

        portCircle.getProperties().put("is_port", true);
        portCircle.getProperties().put("fmc_id", selectedNode.getProperties().get("fmc_id"));
        portCircle.getProperties().put("port_data", p);

        return portCircle;
    }

    private boolean isPort(Node node) {
        return Boolean.TRUE.equals(node.getProperties().get("is_port"));
    }

    private boolean isWaypoint(Node node) {
        return Boolean.TRUE.equals(node.getProperties().get("is_waypoint"));
    }

    @Override public void handleMouseDragged(MouseEvent event, StateContext context) {}
    @Override public void handleMouseReleased(MouseEvent event, StateContext context) {}
}
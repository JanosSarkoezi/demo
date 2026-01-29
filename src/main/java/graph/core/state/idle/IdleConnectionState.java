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

        // 1. FALL: Klick auf einen Port -> Verbindung (Polygon) starten
        if (target instanceof Circle portCircle && isPort(portCircle)) {
            // Wir wechseln in den CreateConnectionState und übergeben den Port
            context.setCurrentState(new ConnectionState(portCircle, mouseInWorld));
            return;
        }

        // 2. FALL: Klick auf einen Waypoint (gelber Knickpunkt eines existierenden Polygons)
        if (target instanceof Circle waypoint && isWaypoint(waypoint)) {
            // Ermöglicht das Verschieben der Knickpunkte
            context.setCurrentState(new MoveState(waypoint, mouseInWorld.getX(), mouseInWorld.getY(), this));
            return;
        }

        // 3. FALL: Klick auf ein normales Shape (Rechteck/Blauer Kreis)
        if (target instanceof Shape clickedShape) {
            sm.toggleSelection(clickedShape);
            refreshPorts(context);
        }
        // 4. FALL: Klick ins Leere -> Alles deselektieren
        else if (target == context.getDrawingPane()) {
            sm.clearSelection();
            refreshPorts(context);
        }
    }

    /**
     * Erzeugt die gelben Ports an den Rändern der selektierten Shapes.
     */
    private void refreshPorts(StateContext context) {
        // UI-Layer leeren
        context.getDrawingPane().getUiLayer().getChildren().clear();

        for (Node selectedNode : context.getSelectionManager().getSelectedNodes()) {
            List<Port> ports = PortCalculator.getPortsForNode(selectedNode);

            for (Port p : ports) {
                // Port-Kreis erstellen
                Circle portCircle = new Circle(6, Color.YELLOW);
                portCircle.setStroke(Color.GOLDENROD);
                portCircle.setStrokeWidth(1.5);

                // WICHTIG: Positionierung relativ zum Shape (ohne Translate)
                // Wir nutzen centerX/Y für die Position am Rand
                portCircle.setCenterX(p.position().getX() - selectedNode.getTranslateX());
                portCircle.setCenterY(p.position().getY() - selectedNode.getTranslateY());

                // Binding: Der Port reitet auf dem Translate des Shapes mit
                portCircle.translateXProperty().bind(selectedNode.translateXProperty());
                portCircle.translateYProperty().bind(selectedNode.translateYProperty());

                // Metadaten setzen
                portCircle.getProperties().put("is_port", true);
                portCircle.getProperties().put("port_data", p);

                // Über die neue addNode Methode der GraphView hinzufügen
                context.getDrawingPane().addNode(portCircle);
            }
        }
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
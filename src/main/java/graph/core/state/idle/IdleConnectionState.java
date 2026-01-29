package graph.core.state.idle;

import graph.core.selection.SelectionManager;
import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.state.active.ConnectionState;
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

        // FALL: Klick auf einen Port -> Verbindung starten
        if (target instanceof Circle portCircle && isPort(portCircle)) {
            // Wir starten den neuen State und übergeben den angeklickten Port
            context.setCurrentState(new ConnectionState(portCircle, mouseInWorld));
            return;
        }

        // FALL: Klick auf ein Shape (aber kein Port) -> Selektion togglen
        if (target instanceof Shape clickedShape) {
            sm.toggleSelection(clickedShape);
            refreshPorts(context);
        }
        // FALL: Klick ins Leere -> Alles deselektieren
        else if (target == context.getDrawingPane()) {
            sm.clearSelection();
            refreshPorts(context);
        }
    }

    /**
     * Leert den UI-Layer und zeichnet die Ports für alle aktuell selektierten Shapes neu.
     * Die Ports werden per Binding an das Shape geklebt.
     */
    private void refreshPorts(StateContext context) {
        // 1. UI-Layer leeren
        context.getDrawingPane().getUiLayer().getChildren().clear();

        for (Node selectedNode : context.getSelectionManager().getSelectedNodes()) {
            List<Port> ports = PortCalculator.getPortsForNode(selectedNode);

            for (Port p : ports) {
                Circle portCircle = new Circle(5, Color.YELLOW);
                portCircle.setStroke(Color.ORANGE);
                portCircle.setStrokeWidth(1.5);

                portCircle.setCenterX(p.position().getX() - selectedNode.getTranslateX());
                portCircle.setCenterY(p.position().getY() - selectedNode.getTranslateY());

                portCircle.translateXProperty().bind(selectedNode.translateXProperty());
                portCircle.translateYProperty().bind(selectedNode.translateYProperty());

                portCircle.getProperties().put("is_port", true);
                portCircle.getProperties().put("port_data", p);

                context.getDrawingPane().getUiLayer().getChildren().add(portCircle);
            }
        }
    }

    private boolean isPort(Node node) {
        return Boolean.TRUE.equals(node.getProperties().get("is_port"));
    }

    @Override public void handleMouseDragged(MouseEvent event, StateContext context) {}
    @Override public void handleMouseReleased(MouseEvent event, StateContext context) {}
}
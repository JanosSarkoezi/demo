package graph.core.state.idle;

import graph.core.selection.SelectionManager;
import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.util.Port;
import graph.core.util.PortCalculator;
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

        if (event.getTarget() instanceof Shape clickedShape && !(clickedShape instanceof Circle port && isPort(port))) {
            // 1. Toggle die Auswahl im Manager (macht auch den goldenen Effekt)
            sm.toggleSelection(clickedShape);

            // 2. Ports basierend auf der neuen Auswahl aktualisieren
            refreshPorts(context);
        }
        else if (event.getTarget() == context.getDrawingPane()) {
            // Klick ins Leere -> Auswahl leeren
            sm.clearSelection();
            refreshPorts(context);
        }
        // Falls auf einen Port geklickt wurde, würde hier später der ConnectionState starten
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
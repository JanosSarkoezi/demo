package graph.core.state;

import graph.controller.MainController;
import graph.model.ConnectionModel;
import graph.model.GraphNode;
import graph.model.WaypointModel;
import graph.view.GraphView;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public record ActiveConnectionState(
        GraphNode startNode,
        int startPortIndex,
        Circle startPort,
        List<WaypointModel> waypoints,
        MainController main
) implements InteractionState, Cleanable {

    @Override
    public InteractionState handleMouseMoved(MouseEvent event, Pane canvas) {
        GraphView view = (GraphView) canvas;
        // Hier rufen wir eine statische Utility-Methode auf,
        // die die Linie auf dem UI-Layer zeichnet/aktualisiert
        ConnectionVisuals.updatePreview(view, startPort, waypoints, event);
        return this;
    }

    @Override
    public InteractionState handleMousePressed(MouseEvent event, Pane canvas) {
        GraphView view = (GraphView) canvas;
        var hit = event.getPickResult().getIntersectedNode();

        // 1. Ziel-Port getroffen?
        if (hit instanceof Circle targetPort && "port".equals(targetPort.getStyleClass().toString())) {
            finishConnection(targetPort, view);
            return getNextBaseState(main); // Wechsel -> Cleanup wird automatisch getriggert!
        }

        // 2. Wegpunkt hinzufügen
        Point2D p = view.getUiLayer().sceneToLocal(event.getSceneX(), event.getSceneY());
        List<WaypointModel> nextWaypoints = new ArrayList<>(waypoints);
        nextWaypoints.add(new WaypointModel(p.getX(), p.getY()));

        return new ActiveConnectionState(startNode, startPortIndex, startPort, nextWaypoints, main);
    }

    @Override
    public void cleanup(Pane canvas) {
        // Diese Methode wird vom Controller aufgerufen, wenn der State wechselt
        ConnectionVisuals.removeVisuals((GraphView) canvas);
        startPort.setFill(Color.CORNFLOWERBLUE);
        startPort.setRadius(5);
    }

    private void finishConnection(Circle targetPort, GraphView view) {
        GraphNode endNode = (GraphNode) targetPort.getProperties().get("node");
        int endPortIndex = (int) targetPort.getProperties().get("portIndex");
        main.getConnectionRenderer().addConnection(new ConnectionModel(
                startNode, startPortIndex, endNode, endPortIndex, new ArrayList<>(waypoints)
        ), view);
    }

    @Override public InteractionState handleMouseDragged(MouseEvent e, Pane c) { return this; }
    @Override public InteractionState handleMouseReleased(MouseEvent e, Pane c) { return this; }
}
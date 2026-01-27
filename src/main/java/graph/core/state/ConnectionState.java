package graph.core.state;

import graph.controller.MainController;
import graph.model.ConnectionModel;
import graph.model.GraphNode;
import graph.model.WaypointModel;
import graph.view.GraphView;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

public record ConnectionState(
        GraphNode startNode,
        int startPortIndex,
        Circle activeStartPort,
        List<WaypointModel> waypoints,
        Polyline previewLine,
        List<Circle> waypointCircles,
        MainController main
) implements InteractionState {

    // Bequemer Konstruktor für den Start im IdleState
    public ConnectionState(GraphNode startNode, int startPortIndex, Circle startPort, MainController main) {
        this(startNode, startPortIndex, startPort, new ArrayList<>(),
                initPreviewLine(), new ArrayList<>(), main);

        // Visuelles Feedback für den Startport
        startPort.setFill(Color.ORANGE);
        startPort.setRadius(8);
    }

    private static Polyline initPreviewLine() {
        Polyline line = new Polyline();
        line.setStroke(Color.CORNFLOWERBLUE);
        line.setStrokeWidth(2.0);
        line.getStrokeDashArray().addAll(6.0, 4.0);
        line.setMouseTransparent(true);
        return line;
    }

    @Override
    public InteractionState handleMousePressed(MouseEvent event, Pane canvas) {
        GraphView view = (GraphView) canvas;

        // Abbrechen mit Rechtsklick
        if (event.getButton() == MouseButton.SECONDARY) {
            cleanupUI(view);
            return getNextBaseState(main);
        }

        var hit = event.getPickResult().getIntersectedNode();

        // 1. Ziel-Port Prüfung
        if (hit instanceof Circle targetPort && "port".equals(targetPort.getStyleClass().toString())) {
            finishConnection(targetPort, view);
            cleanupUI(view);
            return getNextBaseState(main);
        }

        // 2. Wegpunkt hinzufügen (Klick in die Leere)
        Point2D p = view.getUiLayer().sceneToLocal(event.getSceneX(), event.getSceneY());

        // Daten aktualisieren
        List<WaypointModel> nextWaypoints = new ArrayList<>(waypoints);
        nextWaypoints.add(new WaypointModel(p.getX(), p.getY()));

        // UI aktualisieren (gelber Punkt)
        Circle wpCircle = new Circle(p.getX(), p.getY(), 4, Color.GOLD);
        wpCircle.setMouseTransparent(true);
        view.getUiLayer().getChildren().add(wpCircle);

        List<Circle> nextCircles = new ArrayList<>(waypointCircles);
        nextCircles.add(wpCircle);

        // Wir geben einen neuen Record mit den aktualisierten Listen zurück
        return new ConnectionState(startNode, startPortIndex, activeStartPort,
                nextWaypoints, previewLine, nextCircles, main);
    }

    @Override
    public InteractionState handleMouseMoved(MouseEvent event, Pane canvas) {
        GraphView view = (GraphView) canvas;
        Point2D p = view.getUiLayer().sceneToLocal(event.getSceneX(), event.getSceneY());

        // Vorschau-Linie zeichnen
        if (!view.getUiLayer().getChildren().contains(previewLine)) {
            view.getUiLayer().getChildren().add(previewLine);
        }

        previewLine.getPoints().clear();
        // Startpunkt (Port)
        Point2D startP = view.getUiLayer().sceneToLocal(activeStartPort.localToScene(Point2D.ZERO));
        previewLine.getPoints().addAll(startP.getX() + 5, startP.getY() + 5);

        // Alle Zwischen-Wegpunkte
        for (Circle c : waypointCircles) {
            previewLine.getPoints().addAll(c.getCenterX(), c.getCenterY());
        }

        // Maus-Position
        previewLine.getPoints().addAll(p.getX(), p.getY());

        return this;
    }

    private void finishConnection(Circle targetPort, GraphView view) {
        GraphNode endNode = (GraphNode) targetPort.getProperties().get("node");
        int endPortIndex = (int) targetPort.getProperties().get("portIndex");

        ConnectionModel newConn = new ConnectionModel(
                startNode, startPortIndex,
                endNode, endPortIndex,
                new ArrayList<>(waypoints)
        );

        main.getConnectionRenderer().addConnection(newConn, view);
    }

    private void cleanupUI(GraphView view) {
        view.getUiLayer().getChildren().remove(previewLine);
        view.getUiLayer().getChildren().removeAll(waypointCircles);

        if (activeStartPort != null) {
            activeStartPort.setFill(Color.CORNFLOWERBLUE);
            activeStartPort.setRadius(5);
        }
    }

    @Override public InteractionState handleMouseDragged(MouseEvent event, Pane canvas) { return this; }
    @Override public InteractionState handleMouseReleased(MouseEvent event, Pane canvas) { return this; }
}
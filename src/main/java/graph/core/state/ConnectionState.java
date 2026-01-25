package graph.core.state;

import graph.controller.MainController;
import graph.model.GraphNode;
import graph.view.GraphView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import java.util.ArrayList;
import java.util.List;

public class ConnectionState implements InteractionState {
    private final MainController main;
    private final List<Circle> yellowWaypoints = new ArrayList<>();
    private final Polyline previewLine = new Polyline();

    private GraphNode startNode;
    private int startPortIndex = -1;
    private Circle activeStartPort; // Um die Farbe später zurückzusetzen

    public ConnectionState(MainController main) {
        this.main = main;
        this.previewLine.setStroke(Color.CORNFLOWERBLUE);
        this.previewLine.setStrokeWidth(2.0);
        this.previewLine.getStrokeDashArray().addAll(6.0, 4.0);
        this.previewLine.setMouseTransparent(true); // Verhindert, dass die Linie Klicks blockiert
    }

    @Override
    public void handleMousePressed(MouseEvent event, Pane canvas) {
        GraphNode node = findModel(event.getPickResult().getIntersectedNode());

        // 1. Auswahl-Logik (Ctrl + Klick)
        if (event.isControlDown() && node != null) {
            node.setSelected(!node.isSelected());
            main.getSelectionRenderer().updatePorts(main.getGraphModel().getNodes(), (GraphView) canvas);
            return;
        }

        // 2. Klick auf einen Port
        if (event.getTarget() instanceof Circle port && Color.CORNFLOWERBLUE.equals(port.getFill())) {
            if (startNode == null) {
                // START festlegen
                startNode = (GraphNode) port.getProperties().get("node");
                startPortIndex = (int) port.getProperties().get("portIndex");
                activeStartPort = port;

                // Optisches Feedback: Port wird Gold
                port.setFill(Color.GOLD);
                port.setRadius(7);

                // Linie initialisieren (Startpunkt + ein Punkt für die Maus)
                previewLine.getPoints().setAll(port.getCenterX(), port.getCenterY(), event.getX(), event.getY());
                if (!canvas.getChildren().contains(previewLine)) {
                    canvas.getChildren().add(previewLine);
                }
            } else {
                // ENDE festlegen
                finishConnection(port, canvas);
            }
            return;
        }

        // 3. Wegpunkte setzen
        if (startNode != null) {
            createYellowWaypoint(event.getX(), event.getY(), canvas);
        }
    }

    @Override
    public void handleMouseMoved(MouseEvent event, Pane canvas) {
        // Gummiband-Effekt: Der letzte Punkt der Polyline folgt der Maus
        if (startNode != null && !previewLine.getPoints().isEmpty()) {
            int size = previewLine.getPoints().size();
            previewLine.getPoints().set(size - 2, event.getX());
            previewLine.getPoints().set(size - 1, event.getY());
        }
    }

    private void createYellowWaypoint(double x, double y, Pane canvas) {
        Circle waypoint = new Circle(x, y, 4, Color.YELLOW);
        waypoint.setStroke(Color.ORANGE);

        waypoint.setOnMouseDragged(e -> {
            waypoint.setCenterX(e.getX());
            waypoint.setCenterY(e.getY());
            updateLinePath();
        });

        yellowWaypoints.add(waypoint);
        canvas.getChildren().add(waypoint);

        // Füge einen neuen Punkt in die Polyline ein, damit das Gummiband weitergeht
        previewLine.getPoints().addAll(x, y);
    }

    private void updateLinePath() {
        if (previewLine.getPoints().isEmpty()) return;

        // Startpunkt behalten
        double startX = previewLine.getPoints().get(0);
        double startY = previewLine.getPoints().get(1);

        List<Double> points = new ArrayList<>();
        points.add(startX);
        points.add(startY);

        // Alle gelben Punkte hinzufügen
        for (Circle cp : yellowWaypoints) {
            points.add(cp.getCenterX());
            points.add(cp.getCenterY());
        }

        // Den "Maus-Punkt" am Ende wieder hinzufügen (wird durch MouseMoved aktualisiert)
        points.add(points.get(points.size() - 2));
        points.add(points.get(points.size() - 1));

        previewLine.getPoints().setAll(points);
    }

    private void finishConnection(Circle targetPort, Pane canvas) {
        // Aufräumen
        canvas.getChildren().remove(previewLine);
        canvas.getChildren().removeAll(yellowWaypoints);
        if (activeStartPort != null) {
            activeStartPort.setFill(Color.CORNFLOWERBLUE);
            activeStartPort.setRadius(5);
        }

        // Alle Nodes deselektieren
        main.getGraphModel().getNodes().forEach(n -> n.setSelected(false));
        main.getSelectionRenderer().updatePorts(main.getGraphModel().getNodes(), (GraphView) canvas);

        yellowWaypoints.clear();
        startNode = null;
    }

    @Override public void handleMouseDragged(MouseEvent event, Pane canvas) {}
    @Override public void handleMouseReleased(MouseEvent event, Pane canvas) {}
}
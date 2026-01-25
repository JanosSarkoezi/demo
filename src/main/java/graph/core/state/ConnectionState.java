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

    public ConnectionState(MainController main) {
        this.main = main;
        this.previewLine.setStroke(Color.CORNFLOWERBLUE);
        this.previewLine.setStrokeWidth(2.0);
        this.previewLine.getStrokeDashArray().addAll(6.0, 4.0);
        this.previewLine.setMouseTransparent(true); // Verhindert, dass die Linie Klicks abfängt
    }

    @Override
    public void handleMousePressed(MouseEvent event, Pane canvas) {
        // Nutzt die default-Methode aus dem Interface
        GraphNode node = findModel(event.getPickResult().getIntersectedNode());

        // 1. Auswahl-Logik (Ctrl + Klick)
        if (event.isControlDown() && node != null) {
            node.setSelected(!node.isSelected());
            main.getSelectionRenderer().updatePorts(main.getGraphModel().getNodes(), (GraphView) canvas);
            return;
        }

        // 2. Klick auf blauen Port (Start oder Ende)
        if (event.getTarget() instanceof Circle port && Color.CORNFLOWERBLUE.equals(port.getFill())) {
            if (startNode == null) {
                startNode = (GraphNode) port.getProperties().get("node");
                startPortIndex = (int) port.getProperties().get("portIndex");

                // Linie starten: Erster Punkt ist das Zentrum des Ports
                previewLine.getPoints().setAll(port.getCenterX(), port.getCenterY());
                // Zweiten Punkt für das Gummiband zur Maus hinzufügen
                previewLine.getPoints().addAll(event.getX(), event.getY());
                canvas.getChildren().add(previewLine);
            } else {
                finishConnection(port, canvas);
            }
            return;
        }

        // 3. Gelbe Wegpunkte setzen
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
            e.consume();
        });

        yellowWaypoints.add(waypoint);
        canvas.getChildren().add(waypoint);

        // Einen neuen Punkt für das Gummiband hinzufügen
        previewLine.getPoints().addAll(x, y);
        updateLinePath();
    }

    private void updateLinePath() {
        if (previewLine.getPoints().isEmpty()) return;

        // Wir behalten den Startpunkt (Index 0,1) bei
        double startX = previewLine.getPoints().get(0);
        double startY = previewLine.getPoints().get(1);

        List<Double> newPoints = new ArrayList<>();
        newPoints.add(startX);
        newPoints.add(startY);

        for (Circle cp : yellowWaypoints) {
            newPoints.add(cp.getCenterX());
            newPoints.add(cp.getCenterY());
        }

        // Gummiband-Punkt (Ende) hinzufügen, falls wir noch nicht fertig sind
        newPoints.add(newPoints.get(newPoints.size()-2));
        newPoints.add(newPoints.get(newPoints.size()-1));

        previewLine.getPoints().setAll(newPoints);
    }

    private void finishConnection(Circle targetPort, Pane canvas) {
        // TODO: Hier ein neues 'ConnectionModel' Objekt erstellen und dem GraphModel hinzufügen
        // Damit die Verbindung dauerhaft bleibt!

        canvas.getChildren().remove(previewLine);
        canvas.getChildren().removeAll(yellowWaypoints);
        yellowWaypoints.clear();
        startNode = null;

        // Alle Selektionen aufheben nach erfolgreichem Verbinden
        main.getGraphModel().getNodes().forEach(n -> n.setSelected(false));
        main.getSelectionRenderer().updatePorts(main.getGraphModel().getNodes(), (GraphView) canvas);
    }

    @Override public void handleMouseDragged(MouseEvent event, Pane canvas) {}
    @Override public void handleMouseReleased(MouseEvent event, Pane canvas) {}
}
package graph.core.state.active;

import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.state.idle.IdleConnectionState;
import graph.core.util.Port;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.collections.ObservableList;
import java.util.UUID;

public class ConnectionState implements EditorState {
    private final Polyline polyline;
    private final Node startPort;
    private int ghostXIndex;
    private int ghostYIndex;

    public ConnectionState(Node startPort, Point2D startPos) {
        this.startPort = startPort;
        this.polyline = new Polyline();
        this.polyline.setStroke(Color.BLACK);
        this.polyline.setStrokeWidth(2);

        // 1. Startpunkt fest binden
        addBoundPoint(startPort);

        // 2. Gummiband-Punkt initialisieren
        ObservableList<Double> pts = polyline.getPoints();
        ghostXIndex = pts.size();
        ghostYIndex = ghostXIndex + 1;
        pts.addAll(startPos.getX(), startPos.getY());
    }

    @Override
    public void handleMouseMoved(MouseEvent event, StateContext context) {
        Point2D mouse = context.getMouseInWorld(event);
        polyline.getPoints().set(ghostXIndex, mouse.getX());
        polyline.getPoints().set(ghostYIndex, mouse.getY());
    }

    @Override
    public void handleMousePressed(MouseEvent event, StateContext context) {
        Point2D mouse = context.getMouseInWorld(event);
        Node target = (Node) event.getTarget();

        // Polyline beim ersten Klick ins Model/View bringen
        if (polyline.getParent() == null) {
            context.addShapeToModel(polyline);
        }

        // FALL: Klick auf End-Port
        if (isPort(target) && target != startPort) {
            finishConnection(target, context);
            return;
        }

        // FALL: Knickpunkt erzeugen (Klick ins Leere)
        createWaypoint(mouse, context);
    }

    private void createWaypoint(Point2D pos, StateContext context) {
        // Gelber Knickpunkt-Kreis
        Circle waypoint = new Circle(pos.getX(), pos.getY(), 6, Color.YELLOW);
        waypoint.setStroke(Color.GOLDENROD);
        waypoint.getProperties().put("is_waypoint", true); // Wichtig für MoveState!

        // In die View (uiLayer) via GraphView
        context.getDrawingPane().addNode(waypoint);

        // Der aktuelle "Ghost-Punkt" in der Polyline wird jetzt fest an diesen Waypoint gebunden
        bindPointToNode(ghostXIndex, waypoint);

        // Neuen Ghost-Punkt für das nächste Segment erstellen
        ObservableList<Double> pts = polyline.getPoints();
        ghostXIndex = pts.size();
        ghostYIndex = ghostXIndex + 1;
        pts.addAll(pos.getX(), pos.getY());
    }

    private void addBoundPoint(Node node) {
        // 1. Berechne die korrekte Startposition sofort
        Point2D initialPos = getAbsolutePosition(node);

        // 2. Merke dir den Index, wo diese Koordinaten landen werden
        int xIdx = polyline.getPoints().size();

        // 3. Füge die echten Werte hinzu (nicht 0.0)
        polyline.getPoints().addAll(initialPos.getX(), initialPos.getY());

        // 4. Jetzt binde die Listener für zukünftige Bewegungen
        bindPointToNode(xIdx, node);
    }

    // Hilfsmethode für die saubere Positionsbestimmung
    // Hilfsmethode für die saubere Positionsbestimmung basierend auf Zentren
    private Point2D getAbsolutePosition(Node node) {
        double x, y;

        if (node instanceof Circle c) {
            // Für alle Kreise (Ports & Waypoints): Mitte + Verschiebung
            x = c.getCenterX() + c.getTranslateX();
            y = c.getCenterY() + c.getTranslateY();
        } else {
            // Fallback für Rechtecke o.ä.
            x = node.getLayoutX() + node.getTranslateX();
            y = node.getLayoutY() + node.getTranslateY();
        }
        return new Point2D(x, y);
    }

    private void bindPointToNode(int xIdx, Node node) {
        int yIdx = xIdx + 1;

        // Diese Logik wird immer aufgerufen, wenn sich der Port oder das Shape bewegt
        Runnable updatePos = () -> {
            double worldX, worldY;

            if (node instanceof Circle portCircle) {
                // Wir nutzen die echten Daten des Kreises:
                // Center (Position am Rand) + Translate (Bewegung des Vaters)
                worldX = portCircle.getCenterX() + portCircle.getTranslateX();
                worldY = portCircle.getCenterY() + portCircle.getTranslateY();
            } else {
                worldX = node.getLayoutX() + node.getTranslateX();
                worldY = node.getLayoutY() + node.getTranslateY();
            }

            if (xIdx < polyline.getPoints().size()) {
                polyline.getPoints().set(xIdx, worldX);
                polyline.getPoints().set(yIdx, worldY);
            }
        };

        // WICHTIG: Wir binden an alle Properties, die die Weltposition beeinflussen
        node.translateXProperty().addListener((obs, oldV, newV) -> updatePos.run());
        node.translateYProperty().addListener((obs, oldV, newV) -> updatePos.run());

        if (node instanceof Circle c) {
            // Falls sich der Port selbst auf dem Shape verschiebt (z.B. durch Resize)
            c.centerXProperty().addListener((obs, oldV, newV) -> updatePos.run());
            c.centerYProperty().addListener((obs, oldV, newV) -> updatePos.run());
        }

        updatePos.run(); // Sofortige Initialisierung beim Start
    }

    private void finishConnection(Node endPort, StateContext context) {
        // IDs aus den Ports extrahieren
        UUID sourceId = (UUID) startPort.getProperties().get("fmc_id");
        UUID targetId = (UUID) endPort.getProperties().get("fmc_id");

        // Port-Offsets extrahieren
        Port sPortData = (Port) startPort.getProperties().get("port_data");
        Port tPortData = (Port) endPort.getProperties().get("port_data");

        // Wir berechnen die Offsets relativ zur TRANSLATION des Owners (die Translation entspricht der Modell-Position)
        double soX = sPortData.position().getX() - sPortData.owner().getTranslateX();
        double soY = sPortData.position().getY() - sPortData.owner().getTranslateY();
        double toX = tPortData.position().getX() - tPortData.owner().getTranslateX();
        double toY = tPortData.position().getY() - tPortData.owner().getTranslateY();

        try {
            // Neues Verbindungs-Modell erstellen und registrieren
            graph.core.model.Connection conn = new graph.core.model.Connection(sourceId, soX, soY, targetId, toX, toY);

            // Wegpunkte übertragen
            ObservableList<Double> pts = polyline.getPoints();
            for (int i = 2; i < pts.size() - 2; i++) {
                conn.getWaypoints().add(pts.get(i));
            }

            context.getRegistry().addConnection(conn);
        } catch (IllegalArgumentException e) {
            System.err.println("Verbindung abgelehnt: " + e.getMessage());
        }

        context.getDrawingPane().removeNode(polyline);
        context.setCurrentState(new IdleConnectionState());
    }

    private boolean isPort(Node n) {
        return Boolean.TRUE.equals(n.getProperties().get("is_port"));
    }

    @Override public void handleMouseDragged(MouseEvent event, StateContext context) {}
    @Override public void handleMouseReleased(MouseEvent event, StateContext context) {}
}

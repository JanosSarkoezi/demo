package graph.core.state.active;

import graph.core.model.FmcObject;
import graph.core.model.FmcType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectionState implements EditorState {
    private final Polyline polyline;
    private final Node startPort;
    private final List<UUID> waypointIds = new ArrayList<>();
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
        // 1. Neues Modell-Objekt für den Wegpunkt erstellen
        FmcObject waypointObj = new FmcObject(FmcType.WAYPOINT, pos.getX(), pos.getY());
        context.getRegistry().addObject(waypointObj);
        
        // 2. ID merken für die spätere Connection
        waypointIds.add(waypointObj.getId());

        // 3. Für das Gummiband binden wir den Punkt an das Modell-Objekt
        bindPointToNode(ghostXIndex, waypointObj);

        // Neuen Ghost-Punkt für das nächste Segment erstellen
        ObservableList<Double> pts = polyline.getPoints();
        ghostXIndex = pts.size();
        ghostYIndex = ghostXIndex + 1;
        pts.addAll(pos.getX(), pos.getY());
    }

    private void addBoundPoint(Node node) {
        Point2D initialPos = getAbsolutePosition(node);
        int xIdx = polyline.getPoints().size();
        polyline.getPoints().addAll(initialPos.getX(), initialPos.getY());
        bindPointToNode(xIdx, node);
    }

    private Point2D getAbsolutePosition(Node node) {
        double x, y;
        if (node instanceof Circle c) {
            x = c.getCenterX() + c.getTranslateX();
            y = c.getCenterY() + c.getTranslateY();
        } else {
            x = node.getLayoutX() + node.getTranslateX();
            y = node.getLayoutY() + node.getTranslateY();
        }
        return new Point2D(x, y);
    }

    private void bindPointToNode(int xIdx, Object nodeOrObj) {
        int yIdx = xIdx + 1;

        Runnable updatePos;
        if (nodeOrObj instanceof Node node) {
             updatePos = () -> {
                double worldX, worldY;
                if (node instanceof Circle portCircle) {
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
            node.translateXProperty().addListener((obs, oldV, newV) -> updatePos.run());
            node.translateYProperty().addListener((obs, oldV, newV) -> updatePos.run());
            if (node instanceof Circle c) {
                c.centerXProperty().addListener((obs, oldV, newV) -> updatePos.run());
                c.centerYProperty().addListener((obs, oldV, newV) -> updatePos.run());
            }
        } else if (nodeOrObj instanceof FmcObject fmc) {
            updatePos = () -> {
                if (xIdx < polyline.getPoints().size()) {
                    polyline.getPoints().set(xIdx, fmc.getX());
                    polyline.getPoints().set(yIdx, fmc.getY());
                }
            };
            fmc.xProperty().addListener((obs, oldV, newV) -> updatePos.run());
            fmc.yProperty().addListener((obs, oldV, newV) -> updatePos.run());
        } else return;

        updatePos.run();
    }

    private void finishConnection(Node endPort, StateContext context) {
        UUID sourceId = (UUID) startPort.getProperties().get("fmc_id");
        UUID targetId = (UUID) endPort.getProperties().get("fmc_id");

        Port sPortData = (Port) startPort.getProperties().get("port_data");
        Port tPortData = (Port) endPort.getProperties().get("port_data");

        double soX = sPortData.position().getX() - sPortData.owner().getTranslateX();
        double soY = sPortData.position().getY() - sPortData.owner().getTranslateY();
        double toX = tPortData.position().getX() - tPortData.owner().getTranslateX();
        double toY = tPortData.position().getY() - tPortData.owner().getTranslateY();

        try {
            graph.core.model.Connection conn = new graph.core.model.Connection(sourceId, soX, soY, targetId, toX, toY);
            conn.getWaypointIds().addAll(this.waypointIds);
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

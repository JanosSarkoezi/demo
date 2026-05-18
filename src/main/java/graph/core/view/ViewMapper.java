package graph.core.view;

import graph.core.model.Connection;
import graph.core.model.CoreRegistry;
import graph.core.model.FmcObject;
import graph.core.model.FmcType;
import graph.view.GraphView;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ViewMapper {
    private final CoreRegistry registry;
    private final GraphView view;
    private final Map<UUID, Node> nodeMap = new HashMap<>();

    public ViewMapper(CoreRegistry registry, GraphView view) {
        this.registry = registry;
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        registry.getObjects().addListener((MapChangeListener<UUID, FmcObject>) change -> {
            if (change.wasAdded()) createShape(change.getValueAdded());
            if (change.wasRemoved()) removeShape(change.getKey());
        });

        registry.getConnections().addListener((MapChangeListener<UUID, Connection>) change -> {
            if (change.wasAdded()) createConnection(change.getValueAdded());
            if (change.wasRemoved()) removeShape(change.getKey());
        });
    }

    private void createShape(FmcObject obj) {
        Shape shape;
        if (obj.getType() == FmcType.KREIS) {
            Circle circle = new Circle(30, Color.LIGHTBLUE);
            circle.setStroke(Color.STEELBLUE);
            circle.setStrokeWidth(2);
            circle.setCenterX(0);
            circle.setCenterY(0);
            shape = circle;
        } else if (obj.getType() == FmcType.QUADRAT) {
            Rectangle rect = new Rectangle(-40, -30, 80, 60);
            rect.setFill(Color.LIGHTCORAL);
            rect.setStroke(Color.DARKRED);
            rect.setStrokeWidth(2);
            rect.setArcWidth(10);
            rect.setArcHeight(10);
            shape = rect;
        } else { // WAYPOINT
            Circle circle = new Circle(6, Color.YELLOW);
            circle.setStroke(Color.GOLDENROD);
            circle.setStrokeWidth(1.5);
            circle.getProperties().put("is_waypoint", true);
            shape = circle;
        }

        shape.translateXProperty().bindBidirectional(obj.xProperty());
        shape.translateYProperty().bindBidirectional(obj.yProperty());
        shape.getProperties().put("fmc_id", obj.getId());

        nodeMap.put(obj.getId(), shape);
        view.addNode(shape);
    }

    private void createConnection(Connection conn) {
        Polyline polyline = new Polyline();
        polyline.setStroke(Color.BLACK);
        polyline.setStrokeWidth(2);

        FmcObject source = registry.getObjects().get(conn.getSourceId());
        FmcObject target = registry.getObjects().get(conn.getTargetId());

        if (source != null && target != null) {
            ObservableList<Double> points = polyline.getPoints();
            
            // Initialer Aufbau der Punktliste
            rebuildPoints(polyline, conn);

            // Listener auf die Quell- und Ziel-Positionen
            source.xProperty().addListener((obs, old, newVal) -> points.set(0, newVal.doubleValue() + conn.getSourceOffsetX()));
            source.yProperty().addListener((obs, old, newVal) -> points.set(1, newVal.doubleValue() + conn.getSourceOffsetY()));
            
            // Listener auf die Wegpunkte (falls sich die Liste der IDs ändert)
            conn.getWaypointIds().addListener((ListChangeListener<UUID>) c -> rebuildPoints(polyline, conn));

            // Listener für jeden einzelnen Wegpunkt (falls sich dessen Position ändert)
            for (int i = 0; i < conn.getWaypointIds().size(); i++) {
                final int idx = i;
                UUID wpId = conn.getWaypointIds().get(i);
                FmcObject wp = registry.getObjects().get(wpId);
                if (wp != null) {
                    wp.xProperty().addListener((obs, old, newVal) -> points.set(2 + idx * 2, newVal.doubleValue()));
                    wp.yProperty().addListener((obs, old, newVal) -> points.set(2 + idx * 2 + 1, newVal.doubleValue()));
                }
            }

            // Letzter Punkt (Target) muss immer am Ende der Liste sein
            target.xProperty().addListener((obs, old, newVal) -> {
                int lastXIdx = points.size() - 2;
                points.set(lastXIdx, newVal.doubleValue() + conn.getTargetOffsetX());
            });
            target.yProperty().addListener((obs, old, newVal) -> {
                int lastYIdx = points.size() - 1;
                points.set(lastYIdx, newVal.doubleValue() + conn.getTargetOffsetY());
            });
        }

        nodeMap.put(conn.getId(), polyline);
        view.addNode(polyline);
    }

    private void rebuildPoints(Polyline polyline, Connection conn) {
        ObservableList<Double> points = polyline.getPoints();
        points.clear();
        
        FmcObject source = registry.getObjects().get(conn.getSourceId());
        FmcObject target = registry.getObjects().get(conn.getTargetId());
        
        if (source != null && target != null) {
            points.addAll(source.getX() + conn.getSourceOffsetX(), source.getY() + conn.getSourceOffsetY());
            for (UUID wpId : conn.getWaypointIds()) {
                FmcObject wp = registry.getObjects().get(wpId);
                if (wp != null) {
                    points.addAll(wp.getX(), wp.getY());
                }
            }
            points.addAll(target.getX() + conn.getTargetOffsetX(), target.getY() + conn.getTargetOffsetY());
        }
    }

    private void removeShape(UUID id) {
        Node node = nodeMap.remove(id);
        if (node != null) view.removeNode(node);
    }
}

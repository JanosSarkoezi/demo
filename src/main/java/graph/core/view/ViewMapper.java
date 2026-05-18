package graph.core.view;

import graph.core.model.Connection;
import graph.core.model.CoreRegistry;
import graph.core.model.FmcObject;
import graph.core.model.FmcType;
import graph.view.GraphView;
import javafx.collections.MapChangeListener;
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
        // Horche auf neue oder entfernte FmcObjects
        registry.getObjects().addListener((MapChangeListener<UUID, FmcObject>) change -> {
            if (change.wasAdded()) {
                createShape(change.getValueAdded());
            }
            if (change.wasRemoved()) {
                removeShape(change.getKey());
            }
        });

        // Horche auf neue oder entfernte Connections
        registry.getConnections().addListener((MapChangeListener<UUID, Connection>) change -> {
            if (change.wasAdded()) {
                createConnection(change.getValueAdded());
            }
            if (change.wasRemoved()) {
                removeShape(change.getKey());
            }
        });
    }

    private void createShape(FmcObject obj) {
        Shape shape;
        if (obj.getType() == FmcType.KREIS) {
            Circle circle = new Circle(30, Color.LIGHTBLUE);
            circle.setStroke(Color.STEELBLUE);
            circle.setStrokeWidth(2);
            // Zentrierung: Der Kreis wird über translateX/Y bewegt
            circle.centerXProperty().set(0);
            circle.centerYProperty().set(0);
            shape = circle;
        } else {
            Rectangle rect = new Rectangle(80, 60, Color.LIGHTCORAL);
            rect.setStroke(Color.DARKRED);
            rect.setStrokeWidth(2);
            rect.setArcWidth(10);
            rect.setArcHeight(10);
            // Zentrierung: Das Rechteck soll um seinen Mittelpunkt positioniert werden
            rect.xProperty().bind(rect.widthProperty().divide(-2));
            rect.yProperty().bind(rect.heightProperty().divide(-2));
            shape = rect;
        }

        // BINDING: Synchronisiere Model-Position mit View-Position
        shape.translateXProperty().bindBidirectional(obj.xProperty());
        shape.translateYProperty().bindBidirectional(obj.yProperty());

        // ID für spätere Identifikation im UI hinterlegen
        shape.getProperties().put("fmc_id", obj.getId());

        // Speichern und in die View einfügen
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
            // Initialisierung der Punkte
            polyline.getPoints().addAll(source.getX(), source.getY(), target.getX(), target.getY());

            // Listener für automatische Updates
            source.xProperty().addListener((obs, oldVal, newVal) -> polyline.getPoints().set(0, newVal.doubleValue()));
            source.yProperty().addListener((obs, oldVal, newVal) -> polyline.getPoints().set(1, newVal.doubleValue()));
            target.xProperty().addListener((obs, oldVal, newVal) -> polyline.getPoints().set(2, newVal.doubleValue()));
            target.yProperty().addListener((obs, oldVal, newVal) -> polyline.getPoints().set(3, newVal.doubleValue()));
        }

        nodeMap.put(conn.getId(), polyline);
        view.addNode(polyline);
    }

    private void removeShape(UUID id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            view.removeNode(node);
        }
    }
}

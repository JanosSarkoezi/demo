package graph.view;

import graph.controller.MainController;
import graph.core.adapter.ShapeAdapter;
import graph.core.factory.AdapterFactory;
import graph.model.ConnectionModel;
import graph.model.WaypointModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.List;

/**
 * Verantwortlich für die dauerhafte Darstellung einer Verbindung und ihrer Wegpunkte.
 */
public class ConnectionRenderer {

    private final MainController main;

    public ConnectionRenderer(MainController main) {
        this.main = main;
    }

    public void addConnection(ConnectionModel model, GraphView view) {
        Polyline line = new Polyline();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);

        // Adapter für Start- und End-Shapes holen
        ShapeAdapter startAdapter = AdapterFactory.createAdapter(model.getStartNode());
        ShapeAdapter endAdapter = AdapterFactory.createAdapter(model.getEndNode());

        // Bindings für die dynamischen Port-Positionen (Start)
        Observable[] startDeps = startAdapter.getHandleDependencies("ANY");
        DoubleBinding startX = Bindings.createDoubleBinding(() ->
                startAdapter.getPortPosition(model.getStartPortIndex()).getX(), startDeps);
        DoubleBinding startY = Bindings.createDoubleBinding(() ->
                startAdapter.getPortPosition(model.getStartPortIndex()).getY(), startDeps);

        // Bindings für die dynamischen Port-Positionen (Ende)
        Observable[] endDeps = endAdapter.getHandleDependencies("ANY");
        DoubleBinding endX = Bindings.createDoubleBinding(() ->
                endAdapter.getPortPosition(model.getEndPortIndex()).getX(), endDeps);
        DoubleBinding endY = Bindings.createDoubleBinding(() ->
                endAdapter.getPortPosition(model.getEndPortIndex()).getY(), endDeps);

        // Zentraler Trigger für die Neuzeichnung der Linie
        InvalidationListener updateTrigger = obs -> updateLinePoints(line, startX, startY, model.getWaypoints(), endX, endY);

        // Alle Abhängigkeiten registrieren: Start, Ende und alle Wegpunkte
        startX.addListener(updateTrigger);
        startY.addListener(updateTrigger);
        endX.addListener(updateTrigger);
        endY.addListener(updateTrigger);

        for (WaypointModel wp : model.getWaypoints()) {
            wp.xProperty().addListener(updateTrigger);
            wp.yProperty().addListener(updateTrigger);
            // Für jeden Wegpunkt einen interaktiven Griff zeichnen
            drawWaypointHandle(wp, view);
        }

        // Initiales Zeichnen
        updateLinePoints(line, startX, startY, model.getWaypoints(), endX, endY);

        // Die Linie zum permanenten Shape-Layer hinzufügen
        view.getShapeLayer().getChildren().add(line);
    }

    /**
     * Aktualisiert die Punkte der Polyline basierend auf den aktuellen Bindings und Wegpunkten.
     */
    private void updateLinePoints(Polyline line, DoubleBinding sx, DoubleBinding sy,
                                  List<WaypointModel> wps, DoubleBinding ex, DoubleBinding ey) {
        line.getPoints().clear();

        // Startpunkt hinzufügen
        line.getPoints().addAll(sx.get(), sy.get());

        // Wegpunkte hinzufügen
        for (WaypointModel wp : wps) {
            line.getPoints().addAll(wp.getX(), wp.getY());
        }

        // Endpunkt hinzufügen
        line.getPoints().addAll(ex.get(), ey.get());
    }

    /**
     * Erstellt einen interaktiven gelben Kreis, der direkt das WaypointModel manipuliert.
     */
    private void drawWaypointHandle(WaypointModel wp, GraphView view) {
        Circle handle = new Circle(4, Color.YELLOW);
        handle.setStroke(Color.ORANGE);

        // Bindung: Der Kreis folgt dem Modell (bidirektional)
        handle.centerXProperty().bindBidirectional(wp.xProperty());
        handle.centerYProperty().bindBidirectional(wp.yProperty());

        // Lokaler Drag-Handler: Konsumiert Events, damit das State-Pattern nicht stört
        handle.setOnMousePressed(Event::consume);

        handle.setOnMouseDragged(event -> {
            // Umrechnung in Welt-Koordinaten (analog zum MoveState)
            Point2D local = view.getShapeLayer().sceneToLocal(event.getSceneX(), event.getSceneY());

            double targetX = local.getX();
            double targetY = local.getY();

            // Snap-to-Grid Logik aus MoveState übernehmen
            if (main.getToolbar().isStickyActive()) {
                double gridSize = 40.0; // Wert aus deinem MoveState
                targetX = Math.round(targetX / gridSize) * gridSize;
                targetY = Math.round(targetY / gridSize) * gridSize;
            }

            wp.setX(targetX);
            wp.setY(targetY);

            event.consume();
        });

        // Griffe ebenfalls in den Shape-Layer, damit sie permanent sind
        view.getShapeLayer().getChildren().add(handle);
    }
}
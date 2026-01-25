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
import javafx.scene.shape.Polyline;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectionState implements InteractionState {
    private final MainController main;
    private final List<Circle> yellowWaypoints = new ArrayList<>();
    private final Polyline previewLine = new Polyline();

    private GraphNode startNode;
    private int startPortIndex = -1;
    private Circle activeStartPort;
    private final List<WaypointModel> waypointModels = new ArrayList<>();

    public ConnectionState(MainController main) {
        this.main = main;
        this.previewLine.setStroke(Color.CORNFLOWERBLUE);
        this.previewLine.setStrokeWidth(2.0);
        this.previewLine.getStrokeDashArray().addAll(6.0, 4.0);
        this.previewLine.setMouseTransparent(true);
    }

    @Override
    public void handleMousePressed(MouseEvent event, Pane canvas) {
        GraphView view = (GraphView) canvas;
        GraphNode node = findModel(event.getPickResult().getIntersectedNode());

        if (node != null) {
            node.setSelected(!node.isSelected());
            main.getSelectionRenderer().updatePorts(main.getGraphModel().getNodes(), view);
            return;
        }

        // 2. Klick auf einen Port (Start oder Ende)
        if (event.getTarget() instanceof Circle port && Color.CORNFLOWERBLUE.equals(port.getFill())) {
            if (startNode == null) {
                startNode = (GraphNode) port.getProperties().get("node");
                startPortIndex = (int) port.getProperties().get("portIndex"); // Jetzt gespeichert!
                activeStartPort = port;

                port.setFill(Color.GOLD);
                port.setRadius(7);

                previewLine.getPoints().setAll(port.getCenterX(), port.getCenterY(), event.getX(), event.getY());

                if (!view.getUiLayer().getChildren().contains(previewLine)) {
                    view.getUiLayer().getChildren().add(previewLine);
                }
            } else {
                finishConnection(port, view);
            }
            return;
        }

        // 3. Wegpunkte setzen (nur im uiLayer)
        if (startNode != null) {
            createYellowWaypoint(event.getX(), event.getY(), view);
        }
    }

    @Override
    public void handleMouseMoved(MouseEvent event, Pane canvas) {
        if (startNode != null && !previewLine.getPoints().isEmpty()) {
            int size = previewLine.getPoints().size();
            previewLine.getPoints().set(size - 2, event.getX());
            previewLine.getPoints().set(size - 1, event.getY());
        }
    }

    private void createYellowWaypoint(double x, double y, GraphView view) {
        WaypointModel wp = new WaypointModel(x, y);
        waypointModels.add(wp);

        // Temporärer Kreis für die Vorschau
        Circle tempCircle = new Circle(x, y, 4, Color.YELLOW);
        tempCircle.centerXProperty().bindBidirectional(wp.xProperty());
        tempCircle.centerYProperty().bindBidirectional(wp.yProperty());

        view.getUiLayer().getChildren().add(tempCircle);
        yellowWaypoints.add(tempCircle); // Für das Aufräumen in finishConnection

        // Polyline-Punkt hinzufügen
        previewLine.getPoints().addAll(x, y);
        updateLinePath();
    }

    private void updateLinePath() {
        if (previewLine.getPoints().isEmpty()) return;

        double startX = previewLine.getPoints().get(0);
        double startY = previewLine.getPoints().get(1);
        double lastX = previewLine.getPoints().get(previewLine.getPoints().size() - 2);
        double lastY = previewLine.getPoints().get(previewLine.getPoints().size() - 1);

        previewLine.getPoints().clear();
        previewLine.getPoints().addAll(startX, startY);

        for (Circle cp : yellowWaypoints) {
            previewLine.getPoints().addAll(cp.getCenterX(), cp.getCenterY());
        }

        // Den Maus-Punkt am Ende bewahren
        previewLine.getPoints().addAll(lastX, lastY);
    }

    private void finishConnection(Circle targetPort, GraphView view) {
        GraphNode endNode = (GraphNode) targetPort.getProperties().get("node");
        int endPortIndex = (int) targetPort.getProperties().get("portIndex");

        // WICHTIG: Übergib die Liste der WaypointModels direkt, nicht Point2D!
        ConnectionModel newConn = new ConnectionModel(
                startNode, startPortIndex,
                endNode, endPortIndex,
                new ArrayList<>(waypointModels) // Nutze die Modelle statt Point2D
        );

        main.getConnectionRenderer().addConnection(newConn, view);

        // Aufräumen des UI-Layers (die temporären Kreise verschwinden)
        view.getUiLayer().getChildren().remove(previewLine);
        view.getUiLayer().getChildren().removeAll(yellowWaypoints);

        if (activeStartPort != null) {
            activeStartPort.setFill(javafx.scene.paint.Color.CORNFLOWERBLUE);
            activeStartPort.setRadius(5);
        }

        // Zurücksetzen für die nächste Verbindung
        waypointModels.clear();
        yellowWaypoints.clear();
        startNode = null;
    }

    @Override public void handleMouseDragged(MouseEvent event, Pane canvas) {}
    @Override public void handleMouseReleased(MouseEvent event, Pane canvas) {}
}
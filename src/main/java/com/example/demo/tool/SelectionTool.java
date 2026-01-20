package com.example.demo.tool;

import com.example.demo.diagram.connection.ConnectionDot;
import com.example.demo.diagram.connection.SmartConnection;
import com.example.demo.diagram.shape.RectangleAdapter;
import com.example.demo.diagram.shape.ResizeHandle;
import com.example.demo.diagram.shape.ShapeAdapter;
import com.example.demo.model.SelectionModel;
import com.example.demo.tool.state.IdleState;
import com.example.demo.tool.state.SelectionState;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

public class SelectionTool implements Tool {
    private final SelectionModel selectionModel;
    private SelectionState currentState = new IdleState(); // Startzustand

    // Datenfelder (jetzt mit Gettern/Settern für die States)
    private ShapeAdapter currentAdapter;
    private Node target;
    private double anchorX, anchorY;
    private SmartConnection activeConnection;

    // UI-Komponenten bleiben im Tool (Zentrale Anzeige)

    private final Group handleLayer = new Group();
    private final Group connectionLayer = new Group();
    private final Map<String, Rectangle> handleMap = new HashMap<>();
    private final Map<String, Circle> connectionMap = new HashMap<>();

    public SelectionTool(SelectionModel selectionModel) {
        this.selectionModel = selectionModel;
    }

    public void setCurrentState(SelectionState state) {
        System.out.println("Zustandswechsel: " + (currentState != null ? currentState.getClass().getSimpleName() : "null")
                + " -> " + state.getClass().getSimpleName());
        this.currentState = state;
    }

    @Override
    public void onMousePressed(MouseEvent e, Pane c, Group w) {
        currentState.onMousePressed(e, this, w);
    }

    @Override
    public void onMouseDragged(MouseEvent e, Pane c, Group w) {
        currentState.onMouseDragged(e, this, w);
    }

    @Override
    public void onMouseReleased(MouseEvent e, Pane c, Group w) {
        currentState.onMouseReleased(e, this, w);
    }

    // Hilfsmethoden bleiben als "Service" für die States im Tool
    public void updateUI() {
        updateHandlePositions();
        updateConnectionPointPositions();
        updateSmartConnections();
    }

    private void updateSmartConnections() {
        for (SmartConnection conn : selectionModel.getAllConnections()) {
            if (conn.getStartAdapter() == currentAdapter) {
                Point2D startPos = currentAdapter.getConnectionPointPosition(conn.getStartPointName());
                Point2D endPos = conn.getEndAdapter().getConnectionPointPosition(conn.getEndPointName());
                conn.updatePoints(startPos, endPos); //
            } else if (conn.getEndAdapter() == currentAdapter) {
                Point2D startPos = conn.getStartAdapter().getConnectionPointPosition(conn.getStartPointName());
                Point2D endPos = currentAdapter.getConnectionPointPosition(conn.getEndPointName());
                conn.updatePoints(startPos, endPos); //
            }
        }
    }

    private void updateHandlePositions() {
        if (currentAdapter == null || handleMap.isEmpty()) return;

        // Symmetrie: Wir nutzen exakt dieselben Namen wie beim Erstellen
        for (String name : currentAdapter.getHandleNames()) {
            Rectangle rect = handleMap.get(name);
            if (rect != null) {
                Point2D pos = currentAdapter.getHandlePosition(name);
                rect.setX(pos.getX() - ResizeHandle.HANDLE_SIZE / 2);
                rect.setY(pos.getY() - ResizeHandle.HANDLE_SIZE / 2);
            }
        }
    }

    private void updateConnectionPointPositions() {
        if (currentAdapter == null || connectionMap.isEmpty()) return;

        for (String name : currentAdapter.getConnectionPointNames()) {
            Circle dotCircle = connectionMap.get(name);
            if (dotCircle != null) {
                Point2D pos = currentAdapter.getConnectionPointPosition(name);
                dotCircle.setCenterX(pos.getX());
                dotCircle.setCenterY(pos.getY());
            }
        }
    }

    public void editText(ShapeAdapter adapter, Group world) {
        if (adapter == null) return;

        // 1. TextField erstellen und initialisieren
        TextField textField = new TextField(adapter.getText()); // Adapter braucht getText()
        Point2D pos = adapter.getCenter();

        // 2. Positionierung (zentriert über dem Shape)
        textField.setLayoutX(pos.getX() - 50); // Einfache Zentrierung
        textField.setLayoutY(pos.getY() - 15);
        textField.setPrefWidth(100);

        // 3. Fokus und Styling
        textField.requestFocus();

        // 4. Abschluss der Eingabe (Enter oder Fokusverlust)
        textField.setOnAction(e -> commitText(adapter, textField, world));
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) commitText(adapter, textField, world);
        });

        world.getChildren().add(textField);
    }

    private void commitText(ShapeAdapter adapter, TextField textField, Group world) {
        if (world.getChildren().contains(textField)) {
            // 1. Text im Adapter speichern (das Label aktualisiert sich durch Bindings selbst)
            adapter.setText(textField.getText());

            // 2. Das Eingabefeld entfernen
            world.getChildren().remove(textField);

            // Der manuelle Aufruf von ra.updateLabelPosition() kann hier entfallen!
        }
    }

    public void checkTargetHover(MouseEvent event, Group world) {
        // Wir schauen, was unter der Maus liegt
        Node hit = event.getPickResult().getIntersectedNode();

        // Wenn es ein Shape mit Adapter ist und NICHT unser Start-Shape...
        if (hit instanceof Shape s && s.getUserData() instanceof ShapeAdapter targetAdapter) {
            if (targetAdapter != currentAdapter) {
                // Wir "fokussieren" dieses Shape temporär, um seine Dots zu zeigen
                this.currentAdapter = targetAdapter;
                showConnectionPoints(world);
            }
        }
    }

    public void showConnectionPoints(Group world) {
        clearConnectionsFromUI();
        if (currentAdapter == null) return;

        // Wir holen die Punkte vom Adapter (z.B. N, S, E, W)
        for (String name : currentAdapter.getConnectionPointNames()) {
            ConnectionDot dot = new ConnectionDot(name, connectionLayer);
            connectionMap.put(name, dot.getNode());
        }

        updateConnectionPointPositions();

        if (!world.getChildren().contains(connectionLayer)) {
            world.getChildren().add(connectionLayer);
        }
    }

    public void showHandles(Group world) {
        clearHandlesFromUI();
        if (currentAdapter == null) return;

        for (String name : currentAdapter.getHandleNames()) {
            ResizeHandle rh = new ResizeHandle(name, currentAdapter.getHandleCursor(name), handleLayer);
            handleMap.put(name, rh.getNode());
        }
        updateHandlePositions();
        if (!world.getChildren().contains(handleLayer)) world.getChildren().add(handleLayer);
    }

    public void clearHandlesFromUI() {
        handleLayer.getChildren().clear();
    }

    public void clearConnectionsFromUI() {
        connectionLayer.getChildren().clear();
    }

    public Circle findDotAt(Point2D pos) {
        // Wir durchsuchen alle aktuell sichtbaren Connection-Dots
        for (Circle dot : connectionMap.values()) {
            // Wir messen die Distanz zwischen Maus und Kreismittelpunkt
            double distance = pos.distance(dot.getCenterX(), dot.getCenterY());

            // Toleranzbereich: Radius + 2 Pixel Puffer für leichteres Treffen
            if (distance <= ConnectionDot.DOT_RADIUS + 2.0) {
                return dot;
            }
        }
        return null;
    }

    public void clearSelection() {
        // 1. Logische Auswahl im SelectionModel auf null setzen
        if (selectionModel != null) {
            selectionModel.setSelectedAdapter(null);
        }

        // 2. Den internen Zeiger auf den aktuellen Adapter löschen
        this.currentAdapter = null;
        this.target = null;

        // 3. Die UI-Layer für Handles und ConnectionDots leeren
        clearHandlesFromUI();
        clearConnectionsFromUI();

        // 4. Statusnachricht aktualisieren (optional)
        if (selectionModel != null) {
            selectionModel.setStatusMessage("Auswahl aufgehoben.");
        }
    }

    public boolean isConnectionDot(Object target) {
        if (target instanceof Circle circle) {
            return "CONN_POINT".equals(circle.getUserData());
        }
        return false;
    }

    public boolean isHandle(Node node) {
        // Ein Handle ist ein Rectangle, das wir mit einem String-Namen markiert haben
        return node instanceof javafx.scene.shape.Rectangle &&
                node.getUserData() instanceof String;
    }

    public boolean isShape(Node node) {
        // Ein Shape ist daran erkennbar, dass es einen Adapter besitzt
        return node instanceof javafx.scene.shape.Shape &&
                node.getUserData() instanceof ShapeAdapter;
    }

    @Override
    public String getName() {
        return "";
    }

    public SelectionModel getSelectionModel() {
        return selectionModel;
    }

    public SelectionState getCurrentState() {
        return currentState;
    }

    public ShapeAdapter getCurrentAdapter() {
        return currentAdapter;
    }

    public void setCurrentAdapter(ShapeAdapter currentAdapter) {
        this.currentAdapter = currentAdapter;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public double getAnchorX() {
        return anchorX;
    }

    public void setAnchorX(double anchorX) {
        this.anchorX = anchorX;
    }

    public double getAnchorY() {
        return anchorY;
    }

    public void setAnchorY(double anchorY) {
        this.anchorY = anchorY;
    }

    public SmartConnection getActiveConnection() {
        return activeConnection;
    }

    public void setActiveConnection(SmartConnection activeConnection) {
        this.activeConnection = activeConnection;
    }

    public Group getHandleLayer() {
        return handleLayer;
    }

    public Group getConnectionLayer() {
        return connectionLayer;
    }
}
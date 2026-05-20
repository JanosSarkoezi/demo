package graph.controller;

import graph.core.model.CoreRegistry;
import graph.core.selection.SelectionManager;
import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.state.idle.IdleCircleState;
import graph.core.view.ViewMapper;
import graph.view.GraphView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import java.util.UUID;

public class CanvasController implements StateContext {
    private final SelectionManager selectionManager = new SelectionManager();
    private final CoreRegistry registry = new CoreRegistry();
    private final graph.core.command.CommandHistory commandHistory = new graph.core.command.CommandHistory();
    private ViewMapper viewMapper;

    @FXML
    private GraphView drawingPane;
    private EditorState currentState = new IdleCircleState();

    private boolean snapEnabled = true; // Standardmäßig an
    public void setSnapEnabled(boolean enabled) { this.snapEnabled = enabled; }

    @FXML
    public void initialize() {
        // Der ViewMapper wird erst initialisiert, wenn die drawingPane (FXML) da ist.
        this.viewMapper = new ViewMapper(registry, drawingPane);

        // Key-Events für Undo (Ctrl+Z), Redo (Ctrl+Y) und Delete registrieren, sobald die Scene da ist
        drawingPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                    if (event.isControlDown()) {
                        if (event.getCode() == javafx.scene.input.KeyCode.Z) {
                            commandHistory.undo();
                            event.consume();
                        } else if (event.getCode() == javafx.scene.input.KeyCode.Y) {
                            commandHistory.redo();
                            event.consume();
                        }
                    } else if (event.getCode() == javafx.scene.input.KeyCode.DELETE || event.getCode() == javafx.scene.input.KeyCode.BACK_SPACE) {
                        java.util.List<Node> toDelete = new java.util.ArrayList<>(selectionManager.getSelectedNodes());
                        for (Node node : toDelete) {
                            UUID id = (UUID) node.getProperties().get("fmc_id");
                            if (id != null) {
                                if (registry.getObjects().containsKey(id)) {
                                    commandHistory.executeCommand(new graph.core.command.RemoveObjectCommand(registry, id));
                                } else if (registry.getConnections().containsKey(id)) {
                                    commandHistory.executeCommand(new graph.core.command.RemoveConnectionCommand(registry, id));
                                }
                            }
                        }
                        selectionManager.clearSelection();
                        drawingPane.getUiLayer().getChildren().clear();
                        event.consume();
                    }
                });
            }
        });

        // Listener, um bei Entfernen eines Objekts aus der Registry die Auswahl aufzuräumen und Ports zu aktualisieren
        registry.getObjects().addListener((javafx.collections.MapChangeListener<UUID, graph.core.model.FmcObject>) change -> {
            if (change.wasRemoved()) {
                UUID removedId = change.getKey();
                Node selectedNodeToRemove = null;
                for (Node node : selectionManager.getSelectedNodes()) {
                    if (removedId.equals(node.getProperties().get("fmc_id"))) {
                        selectedNodeToRemove = node;
                        break;
                    }
                }
                if (selectedNodeToRemove != null) {
                    selectionManager.getSelectedNodes().remove(selectedNodeToRemove);
                    selectedNodeToRemove.setEffect(null);
                }
                refreshPorts();
            }
        });
    }

    @Override
    public graph.core.command.CommandHistory getCommandHistory() {
        return commandHistory;
    }

    @Override
    public GraphView getDrawingPane() {
        return drawingPane;
    }

    @Override
    public void setCurrentState(EditorState state) {
        this.currentState = state;
        refreshPorts();
    }

    @Override
    public void refreshPorts() {
        drawingPane.getUiLayer().getChildren().clear();
        if (currentState instanceof graph.core.state.idle.IdleConnectionState || currentState instanceof graph.core.state.active.ConnectionState) {
            for (Node selectedNode : selectionManager.getSelectedNodes()) {
                java.util.List<graph.core.util.Port> ports = graph.core.util.PortCalculator.getPortsForNode(selectedNode);
                for (graph.core.util.Port p : ports) {
                    javafx.scene.shape.Circle portCircle = createPortCircle(p, selectedNode);
                    drawingPane.getUiLayer().getChildren().add(portCircle);
                }
            }
        }
    }

    private javafx.scene.shape.Circle createPortCircle(graph.core.util.Port p, Node selectedNode) {
        javafx.scene.shape.Circle portCircle = new javafx.scene.shape.Circle(6, javafx.scene.paint.Color.YELLOW);
        portCircle.setStroke(javafx.scene.paint.Color.GOLDENROD);
        portCircle.setStrokeWidth(1.5);

        portCircle.setCenterX(p.position().getX() - selectedNode.getTranslateX());
        portCircle.setCenterY(p.position().getY() - selectedNode.getTranslateY());

        portCircle.translateXProperty().bind(selectedNode.translateXProperty());
        portCircle.translateYProperty().bind(selectedNode.translateYProperty());

        portCircle.getProperties().put("is_port", true);
        portCircle.getProperties().put("fmc_id", selectedNode.getProperties().get("fmc_id"));
        portCircle.getProperties().put("port_data", p);

        return portCircle;
    }

    @FXML
    void onMousePressed(MouseEvent event) {
        currentState.handleMousePressed(event, this);
        event.consume();
    }

    @FXML
    void onMouseDragged(MouseEvent event) {
        currentState.handleMouseDragged(event, this);
        event.consume();
    }

    @FXML
    void onMouseReleased(MouseEvent event) {
        currentState.handleMouseReleased(event, this);
        event.consume();
    }

    @FXML
    void handleScroll(ScrollEvent event) {
        if (drawingPane instanceof GraphView graphView) {
            graphView.handleZoom(event);
        }
        event.consume();
    }

    @Override
    public void addShapeToModel(Node shape) {
        // Diese Methode wird aus Kompatibilitätsgründen behalten,
        // sollte aber langfristig durch registry-Aufrufe ersetzt werden.
        drawingPane.addNode(shape);
    }

    @Override
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    @Override
    public boolean isSnapToGridEnabled() {
        return snapEnabled;
    }

    @Override
    public CoreRegistry getRegistry() {
        return registry;
    }
}
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

public class CanvasController implements StateContext {
    private final SelectionManager selectionManager = new SelectionManager();
    private final CoreRegistry registry = new CoreRegistry();
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
    }

    @Override
    public GraphView getDrawingPane() {
        return drawingPane;
    }

    @Override
    public void setCurrentState(EditorState state) {
        this.currentState = state;
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
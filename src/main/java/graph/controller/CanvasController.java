package graph.controller;

import graph.core.selection.SelectionManager;
import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.state.idle.IdleCircleState;
import graph.model.DrawingModel;
import graph.view.GraphView;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class CanvasController implements StateContext {
    private final SelectionManager selectionManager = new SelectionManager();

    @FXML
    private GraphView drawingPane;
    private EditorState currentState = new IdleCircleState();
    private DrawingModel model;
    private boolean snapEnabled = true; // Standardmäßig an
    public void setSnapEnabled(boolean enabled) { this.snapEnabled = enabled; }

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

    public void setModel(DrawingModel model) {
        this.model = model;

        // Der Listener reagiert auf JEDE Änderung in der Liste des Models
        model.getShapes().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(drawingPane::addNode);
                }
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(drawingPane::removeNode);
                }
            }
        });
    }

    @Override
    public void addShapeToModel(Node shape) {
        model.addShape(shape);
    }

    @Override
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    @Override
    public boolean isSnapToGridEnabled() {
        return snapEnabled;
    }
}
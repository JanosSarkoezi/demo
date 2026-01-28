package graph.controller;

import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.core.state.idle.IdleCircleState;
import graph.model.DrawingModel;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class CanvasController implements StateContext {
    @FXML private Pane drawingPane;
    private EditorState currentState = new IdleCircleState();
    private DrawingModel model;

    @Override public Pane getDrawingPane() { return drawingPane; }

    @Override public void setCurrentState(EditorState state) { this.currentState = state; }

    @FXML void onMousePressed(MouseEvent e) { currentState.handleMousePressed(e, this); }
    @FXML void onMouseDragged(MouseEvent e) { currentState.handleMouseDragged(e, this); }
    @FXML void onMouseReleased(MouseEvent e) { currentState.handleMouseReleased(e, this); }

    public void setModel(DrawingModel model) {
        this.model = model;

        // Der Listener: Synchronisiere Model -> View
        model.getShapes().addListener((ListChangeListener<? super Node>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    drawingPane.getChildren().addAll(change.getAddedSubList());
                }
                if (change.wasRemoved()) {
                    drawingPane.getChildren().removeAll(change.getRemoved());
                }
            }
        });
    }

    @Override
    public void addShapeToModel(Node shape) {
        model.addShape(shape);
    }
}
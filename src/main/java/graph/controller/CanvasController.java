package graph.controller;

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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class CanvasController implements StateContext {
    @FXML private GraphView drawingPane;
    private EditorState currentState = new IdleCircleState();
    private DrawingModel model;

    @Override public GraphView getDrawingPane() { return drawingPane; }

    @Override public void setCurrentState(EditorState state) { this.currentState = state; }

    @FXML void onMousePressed(MouseEvent e) { currentState.handleMousePressed(e, this); }
    @FXML void onMouseDragged(MouseEvent e) { currentState.handleMouseDragged(e, this); }
    @FXML void onMouseReleased(MouseEvent e) { currentState.handleMouseReleased(e, this); }
    @FXML void handleScroll(ScrollEvent event) {
        if (drawingPane instanceof GraphView graphView) {
            graphView.handleZoom(event);
        }
    }

    public void setModel(DrawingModel model) {
        this.model = model;

        // Der Listener reagiert auf JEDE Änderung in der Liste des Models
        model.getShapes().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                // FALL 1: Neue Objekte wurden dem Model hinzugefügt
                if (change.wasAdded()) {
                    for (Node node : change.getAddedSubList()) {
                        addNodeToAppropriateLayer(node);
                    }
                }

                // FALL 2: Objekte wurden aus dem Model entfernt
                if (change.wasRemoved()) {
                    for (Node node : change.getRemoved()) {
                        removeNodeFromLayers(node);
                    }
                }
            }
        });
    }

    /**
     * Hilfsmethode, um zu entscheiden, welcher Layer ein Objekt aufnehmen soll.
     */
    private void addNodeToAppropriateLayer(Node node) {
        if (drawingPane instanceof GraphView gv) {
            // Logik zur Einsortierung:
            if (node instanceof Circle || node instanceof Rectangle) {
                gv.getShapeLayer().getChildren().add(node);
            } else if (node instanceof Line || node instanceof javafx.scene.shape.Polyline) {
                gv.getConnectionLayer().getChildren().add(node);
            } else if (node instanceof javafx.scene.text.Text) {
                gv.getTextLayer().getChildren().add(node);
            } else {
                // Standardmäßig in den Shape-Layer, falls unbekannt
                gv.getShapeLayer().getChildren().add(node);
            }
        }
    }

    /**
     * Hilfsmethode zum sauberen Entfernen aus allen Layern.
     */
    private void removeNodeFromLayers(Node node) {
        if (drawingPane instanceof GraphView gv) {
            gv.getShapeLayer().getChildren().remove(node);
            gv.getConnectionLayer().getChildren().remove(node);
            gv.getTextLayer().getChildren().remove(node);
            gv.getUiLayer().getChildren().remove(node);
        }
    }

    @Override
    public void addShapeToModel(Node shape) {
        model.addShape(shape);
    }
}
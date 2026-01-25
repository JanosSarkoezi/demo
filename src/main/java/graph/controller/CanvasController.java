package graph.controller;

import graph.core.state.IdleState;
import graph.core.state.InteractionState;
import graph.view.GraphView;
import javafx.fxml.FXML;

public class CanvasController {
    @FXML private GraphView graphView; // Wird via FXML injiziert

    private InteractionState currentState;
    private MainController main;

    public void init(MainController main) {
        this.main = main;
        // Wir starten immer im IdleState
        this.currentState = new IdleState(main);

        if (graphView != null) {
            setupEvents();
        }
    }

    private void setupEvents() {
        // Der Controller fängt die Events ab und reicht sie an den State weiter
        graphView.setOnMousePressed(e -> currentState.handleMousePressed(e, graphView));
        graphView.setOnMouseDragged(e -> currentState.handleMouseDragged(e, graphView));
        graphView.setOnMouseReleased(e -> currentState.handleMouseReleased(e, graphView));
        graphView.setOnMouseMoved(e -> currentState.handleMouseMoved(e, graphView));
        graphView.setOnScroll(graphView::handleZoom);
    }

    public void setCurrentState(InteractionState newState) {
        this.currentState = newState;
    }

    // Zugriffsmethoden für die States (stark vereinfacht)
    public GraphView getView() { return graphView; }
}
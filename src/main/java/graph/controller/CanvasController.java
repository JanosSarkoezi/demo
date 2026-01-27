package graph.controller;

import graph.core.state.Cleanable;
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
        // Die Einzeiler, die du magst – jetzt mit Zuweisung!
        graphView.setOnMousePressed(e -> {
            setCurrentState(currentState.handleMousePressed(e, graphView));
            e.consume();
        });
        graphView.setOnMouseDragged(e -> {
            setCurrentState(currentState.handleMouseDragged(e, graphView));
            e.consume();
        });
        graphView.setOnMouseReleased(e -> {
            setCurrentState(currentState.handleMouseReleased(e, graphView));
            e.consume();
        });

        graphView.setOnMouseMoved(e -> {
            setCurrentState(currentState.handleMouseMoved(e, graphView));
        });

        graphView.setOnScroll(graphView::handleZoom);
    }

//    private void updateState(InteractionState nextState) {
//        if (currentState instanceof Cleanable old && nextState.getClass() != currentState.getClass()) {
//            old.cleanup(graphView);
//        }
//        this.currentState = nextState;
//    }

    public void setCurrentState(InteractionState nextState) {
        if (nextState == null) return;

        // --- ZENTRALES LOGGING ---
        String oldStateName = (currentState != null) ? currentState.getClass().getSimpleName() : "null";
        String newStateName = nextState.getClass().getSimpleName();
        String currentTool = main.getToolbar().getSelectedTool();

        if (!oldStateName.equals(newStateName)) {
            System.out.printf("[STATE CHANGE] %s -> %s (Tool: %s)%n",
                    oldStateName, newStateName, currentTool);
        }
        // -------------------------

        if (currentState instanceof Cleanable old && nextState.getClass() != currentState.getClass()) {
            old.cleanup(graphView);
        }

        this.currentState = nextState;
    }

    // Zugriffsmethoden für die States (stark vereinfacht)
    public GraphView getView() { return graphView; }
}
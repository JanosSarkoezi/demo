package graph.core.state;

import graph.controller.MainController;
import graph.model.GraphNode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public interface InteractionState {
    // Rückgabewert geändert von void auf InteractionState
    InteractionState handleMousePressed(MouseEvent event, Pane canvas);
    InteractionState handleMouseDragged(MouseEvent event, Pane canvas);
    InteractionState handleMouseReleased(MouseEvent event, Pane canvas);

    default InteractionState handleMouseMoved(MouseEvent event, Pane canvas) {
        return this;
    }

    // Zentralisiert die Tool-Prüfung aus MoveState/CreateNodeState
    default InteractionState getNextBaseState(MainController main) {
        String tool = main.getToolbar().getSelectedTool();
        return switch (tool) {
            case "CIRCLE", "RECTANGLE" -> new CreateNodeState(main);
            case "CONNECT" -> new InitialConnectionState(main);
            default -> new IdleState(main);
        };
    }

    default GraphNode findModel(javafx.scene.Node node) {
        if (node == null) return null;
        if (node.getUserData() instanceof GraphNode) {
            return (GraphNode) node.getUserData();
        }
        return findModel(node.getParent());
    }
}
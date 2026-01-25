package graph.core.state;

import graph.model.GraphNode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public interface InteractionState {
    void handleMousePressed(MouseEvent event, Pane canvas);
    void handleMouseDragged(MouseEvent event, Pane canvas);
    void handleMouseReleased(MouseEvent event, Pane canvas);

    default void handleMouseMoved(MouseEvent event, Pane canvas) {}

    default GraphNode findModel(javafx.scene.Node node) {
        if (node == null) return null;
        if (node.getUserData() instanceof GraphNode) {
            return (GraphNode) node.getUserData();
        }
        return findModel(node.getParent());
    }
}
package graph.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SelectionModel {
    // Die Quelle der Wahrheit: Was ist gerade selektiert?
    private final ObjectProperty<GraphNode> selectedNode = new SimpleObjectProperty<>(null);

    public void select(GraphNode node) {
        selectedNode.set(node);
    }

    public void clear() {
        selectedNode.set(null);
    }

    public GraphNode getSelectedNode() {
        return selectedNode.get();
    }

    public ObjectProperty<GraphNode> selectedNodeProperty() {
        return selectedNode;
    }
}
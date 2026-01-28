package graph.core.selection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class SelectionManager {
    // Die Liste der aktuell ausgewählten Shapes
    private final ObservableList<Node> selectedNodes = FXCollections.observableArrayList();

    // Ein kleiner visueller Effekt, um die Auswahl zu markieren
    private static final DropShadow SELECTION_EFFECT = new DropShadow(10, Color.GOLD);

    public void toggleSelection(Node node) {
        if (selectedNodes.contains(node)) {
            selectedNodes.remove(node);
            node.setEffect(null);
        } else {
            selectedNodes.add(node);
            node.setEffect(SELECTION_EFFECT);
        }
    }

    public void clearSelection() {
        for (Node node : selectedNodes) {
            node.setEffect(null);
        }
        selectedNodes.clear();
    }

    public ObservableList<Node> getSelectedNodes() {
        return selectedNodes;
    }

    public boolean isSelected(Node node) {
        return selectedNodes.contains(node);
    }
}
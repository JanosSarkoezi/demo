package graph.core.state;

import graph.core.selection.SelectionManager;
import graph.view.GraphView;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public interface StateContext {
    GraphView getDrawingPane();
    void setCurrentState(EditorState newState);
    void addShapeToModel(Node shape);
    SelectionManager getSelectionManager();
    boolean isSnapToGridEnabled();

    default Point2D getMouseInWorld(MouseEvent event) {
        return getDrawingPane().getMouseInWorld(event.getSceneX(), event.getSceneY());
    }
}
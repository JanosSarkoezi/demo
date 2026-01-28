package graph.core.state;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public interface StateContext {
    Pane getDrawingPane();
    void setCurrentState(EditorState newState);
    void addShapeToModel(Node shape);
}
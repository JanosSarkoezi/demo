package graph.core.state;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public interface InteractionState {
    void handleMousePressed(MouseEvent event, Pane canvas);
    void handleMouseDragged(MouseEvent event, Pane canvas);
    void handleMouseReleased(MouseEvent event, Pane canvas);
}
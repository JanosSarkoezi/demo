package graph.core.state;

import javafx.scene.input.MouseEvent;

public interface EditorState {
    void handleMousePressed(MouseEvent event, StateContext context);
    void handleMouseDragged(MouseEvent event, StateContext context);
    void handleMouseReleased(MouseEvent event, StateContext context);

    // Default-Implementierung, da nicht jeder State "Moved" braucht
    default void handleMouseMoved(MouseEvent event, StateContext context) {}
}
package graph.core.state.active;

import graph.core.state.EditorState;
import graph.core.state.StateContext;
import graph.view.GraphView;
import javafx.scene.input.MouseEvent;

public class PanningState implements EditorState {
    private double lastMouseX;
    private double lastMouseY;
    private final EditorState originState;

    public PanningState(double startSceneX, double startSceneY, EditorState origin) {
        this.lastMouseX = startSceneX;
        this.lastMouseY = startSceneY;
        this.originState = origin;
    }

    @Override
    public void handleMouseDragged(MouseEvent event, StateContext context) {
        GraphView gv = context.getDrawingPane();

        double deltaX = event.getSceneX() - lastMouseX;
        double deltaY = event.getSceneY() - lastMouseY;

        // Verschiebe die gesamte Welt-Gruppe
        gv.getWorld().setTranslateX(gv.getWorld().getTranslateX() + deltaX);
        gv.getWorld().setTranslateY(gv.getWorld().getTranslateY() + deltaY);

        lastMouseX = event.getSceneX();
        lastMouseY = event.getSceneY();
    }

    @Override
    public void handleMouseReleased(MouseEvent event, StateContext context) {
        context.setCurrentState(originState);
    }

    @Override public void handleMousePressed(MouseEvent event, StateContext context) {}
}
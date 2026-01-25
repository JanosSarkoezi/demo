package graph.core.state;

import graph.controller.MainController;
import graph.model.RectangleModel;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class TextInputState implements InteractionState {
    private final MainController main;
    private final RectangleModel model;
    private final StackPane view;

    public TextInputState(RectangleModel model, StackPane view, MainController main) {
        this.model = model;
        this.view = view;
        this.main = main;
    }

    @Override
    public void handleMousePressed(MouseEvent event, Pane canvas) {
        TextArea textArea = (TextArea) view.getChildren().stream()
                .filter(n -> n instanceof TextArea)
                .findFirst().orElse(null);

        if (textArea != null) {
            textArea.setMouseTransparent(false);
            textArea.setEditable(true);
            textArea.requestFocus();
        }
    }

    @Override public void handleMouseDragged(MouseEvent event, Pane canvas) {}
    @Override public void handleMouseReleased(MouseEvent event, Pane canvas) {
        TextArea textArea = (TextArea) view.getChildren().stream()
                .filter(n -> n instanceof TextArea)
                .findFirst().orElse(null);

        if (textArea != null) {
            textArea.setEditable(false);
            textArea.setMouseTransparent(true);
            main.getCanvas().setCurrentState(new IdleState(main));
        }
    }
}
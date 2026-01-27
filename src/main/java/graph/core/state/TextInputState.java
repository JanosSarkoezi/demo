package graph.core.state;

import graph.controller.MainController;
import graph.model.RectangleModel;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public record TextInputState(
        RectangleModel model,
        StackPane view,
        MainController main
) implements InteractionState {

    @Override
    public InteractionState handleMousePressed(MouseEvent event, Pane canvas) {
        TextArea textArea = findTextArea();

        if (textArea != null) {
            textArea.setMouseTransparent(false);
            textArea.setEditable(true);
            textArea.requestFocus();
            // Wir bleiben in diesem State, solange wir tippen
            return this;
        }

        // Falls kein TextArea gefunden wurde (sollte nicht passieren), abbrechen
        return getNextBaseState(main);
    }

    @Override
    public InteractionState handleMouseReleased(MouseEvent event, Pane canvas) {
        // In deinem alten Code hast du hier beendet.
        // Oft will man aber erst beenden, wenn man *außerhalb* klickt.
        // Für den Moment behalten wir deine Logik bei:
        stopEditing();
        return getNextBaseState(main);
    }

    @Override
    public InteractionState handleMouseDragged(MouseEvent event, Pane canvas) {
        return this;
    }

    // Hilfsmethode zum "Sperren" des Textfeldes
    private void stopEditing() {
        TextArea textArea = findTextArea();
        if (textArea != null) {
            textArea.setEditable(false);
            textArea.setMouseTransparent(true);
        }
    }

    private TextArea findTextArea() {
        return (TextArea) view.getChildren().stream()
                .filter(n -> n instanceof TextArea)
                .findFirst().orElse(null);
    }
}
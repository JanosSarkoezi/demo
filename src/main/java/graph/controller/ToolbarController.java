package graph.controller;

import graph.core.ToolType;
import javafx.fxml.FXML;
import java.util.function.Consumer;

public class ToolbarController {

    private Consumer<ToolType> onToolSelectedListener;

    // Der MainController registriert sich hier als Listener
    public void setOnToolSelected(Consumer<ToolType> listener) {
        this.onToolSelectedListener = listener;
    }

    @FXML
    private void onCircleClick() {
        notifyListener(ToolType.CIRCLE);
    }

    @FXML
    private void onRectClick() {
        notifyListener(ToolType.RECTANGLE);
    }

    @FXML
    private void onConnClick() {
        notifyListener(ToolType.CONNECTION);
    }

    private void notifyListener(ToolType tool) {
        if (onToolSelectedListener != null) {
            onToolSelectedListener.accept(tool);
        }
    }
}
package graph.controller;

import graph.core.ToolType;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import java.util.function.Consumer;

public class ToolbarController {

    private Consumer<ToolType> onToolSelectedListener;
    private Consumer<Boolean> onSnapChangedListener;

    @FXML
    private CheckBox snapToGridCheckbox;

    // Der MainController registriert sich hier als Listener
    public void setOnToolSelected(Consumer<ToolType> listener) {
        this.onToolSelectedListener = listener;
    }

    public void setOnSnapChanged(Consumer<Boolean> listener) {
        this.onSnapChangedListener = listener;
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

    @FXML
    private void onSnapToGridAction() {
        if (onSnapChangedListener != null) {
            onSnapChangedListener.accept(snapToGridCheckbox.isSelected());
        }
    }

    private void notifyListener(ToolType tool) {
        if (onToolSelectedListener != null) {
            onToolSelectedListener.accept(tool);
        }
    }
}
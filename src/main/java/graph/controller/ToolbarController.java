package graph.controller;

import graph.core.state.CreateNodeState;
import graph.core.state.IdleState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ToolbarController {
    @FXML private ToggleGroup toolGroup;

    @FXML private ToggleButton circleButton;
    @FXML private ToggleButton rectButton;
    @FXML private ToggleButton connectButton;

    @FXML private CheckBox snapToGridCheckbox;
    @FXML private CheckBox stickyCheckbox;

    private MainController main;

    public String getSelectedTool() {
        ToggleButton selected = (ToggleButton) toolGroup.getSelectedToggle();
        if (selected == null) return "NONE";

        if (selected == circleButton) return "CIRCLE";
        if (selected == rectButton) return "RECTANGLE";
        if (selected == connectButton) return "CONNECT";

        return "NONE";
    }

    public void init(MainController main) {
        this.main = main;
    }

    @FXML
    private void onShapeToolSelected(ActionEvent event) {
        main.updateCanvasState();
    }

    public boolean isStickyActive() {
        return stickyCheckbox.isSelected();
    }

    public boolean isSnapToGridActive() {
        return snapToGridCheckbox.isSelected();
    }
}
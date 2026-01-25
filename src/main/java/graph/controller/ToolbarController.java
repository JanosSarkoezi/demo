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
    @FXML private CheckBox snapToGridCheckbox;
    @FXML private CheckBox stickyCheckbox;

    private MainController main;

    public String getSelectedTool() {
        ToggleButton selected = (ToggleButton) toolGroup.getSelectedToggle();
        if (selected == null) return "NONE";
        if (selected == circleButton) return "CIRCLE";
        if (selected == rectButton) return "RECTANGLE";
        return "NONE";
    }

    public void init(MainController main) {
        this.main = main;
    }

    @FXML
    private void onShapeToolSelected(ActionEvent event) {
        if (toolGroup.getSelectedToggle() != null) {
            main.getCanvas().setCurrentState(new CreateNodeState(main));
        } else {
            // Button wurde deselektiert -> Zurück in den Leerlauf
            main.getCanvas().setCurrentState(new IdleState(main));
        }
    }

    public boolean isStickyActive() {
        return stickyCheckbox.isSelected();
    }

    public boolean isSnapToGridActive() {
        return snapToGridCheckbox.isSelected();
    }
}
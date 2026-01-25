module graph {
    requires javafx.controls;
    requires javafx.fxml;

    exports graph;
    opens graph.view to javafx.fxml;
    opens graph.controller to javafx.fxml;
    opens graph.model to javafx.base, javafx.fxml;
}
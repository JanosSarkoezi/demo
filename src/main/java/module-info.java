module graph {
    requires javafx.controls;
    requires javafx.fxml;

    // Das Hauptpaket exportieren
    exports graph;

    // Die Unterpakete für FXML öffnen
    opens graph.controller to javafx.fxml;
    opens graph.model to javafx.base; // Falls du TableViews o.ä. nutzt
}
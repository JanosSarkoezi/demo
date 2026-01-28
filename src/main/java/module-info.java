module graph {
    // Benötigte JavaFX Module
    requires javafx.controls;
    requires javafx.fxml;

    // Erlaube dem FXML-Loader Zugriff auf deine Controller-Klassen (Reflection)
    opens graph.controller to javafx.fxml;

    // Erlaube dem FXML-Loader den Zugriff auf die Main-Klasse, falls dort FXML geladen wird
    opens graph to javafx.graphics, javafx.fxml;

    // Exportiere Pakete, falls andere Module darauf zugreifen müssten (optional für dieses Projekt)
    exports graph;
    exports graph.controller;
    exports graph.core;
    exports graph.core.state;
}
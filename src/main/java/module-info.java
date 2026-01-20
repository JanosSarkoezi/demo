module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.demo.app;
    opens com.example.demo.app to javafx.fxml;
    exports com.example.demo.controller;
    opens com.example.demo.controller to javafx.fxml;
    exports com.example.demo.diagram.connection;
    opens com.example.demo.diagram.connection to javafx.fxml;
    exports com.example.demo.diagram.shape;
    opens com.example.demo.diagram.shape to javafx.fxml;
    exports com.example.demo.model;
    opens com.example.demo.model to javafx.fxml;
    exports com.example.demo.tool;
    opens com.example.demo.tool to javafx.fxml;
    exports com.example.demo.tool.state;
    opens com.example.demo.tool.state to javafx.fxml;
    exports com.example.demo.ui;
    opens com.example.demo.ui to javafx.fxml;
}
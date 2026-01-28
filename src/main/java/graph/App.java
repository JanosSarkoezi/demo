package graph;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graph/main-view.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("JavaFX State Machine Editor");
        stage.show();
    }
    public static void main(String[] args) { launch(); }
}
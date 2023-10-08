package se233.project_watermark;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Menu-view.fxml"));
        stage.setTitle("Menu");
        stage.setScene(new Scene(root));
        stage.show();
    }
}

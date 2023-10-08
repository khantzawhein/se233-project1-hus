    module se233.project_watermark {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;


    opens se233.project_watermark to javafx.fxml;
    exports se233.project_watermark;
}
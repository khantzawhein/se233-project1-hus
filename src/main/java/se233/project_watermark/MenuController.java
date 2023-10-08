package se233.project_watermark;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Enumeration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    @FXML
    private Button WaterMarkButton;
    @FXML
    private Button ResizeButton;
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ListView<File> ListView;
    private List<File> files = new ArrayList<>();
    @FXML
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }
    @FXML
    private void handleDragAndDrop(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            List<File> newFiles = event.getDragboard().getFiles();

            for (File file : newFiles) {
                String fileName = file.getName().toLowerCase();

                if (fileName.endsWith(".zip")) {
                    try {
                        // Create a temporary directory to extract files
                        File tempDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "temp_images");
                        tempDir.mkdir();

                        // Extract the contents of the zip file
                        ZipFile zipFile = new ZipFile(file);
                        Enumeration<? extends ZipEntry> entries = zipFile.entries();

                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            File entryFile = new File(tempDir, entry.getName());

                            // Only extract files (not directories)
                            if (!entry.isDirectory()) {
                                InputStream inputStream = zipFile.getInputStream(entry);
                                OutputStream outputStream = new FileOutputStream(entryFile);

                                byte[] buffer = new byte[1024];
                                int length;
                                while ((length = inputStream.read(buffer)) > 0) {
                                    outputStream.write(buffer, 0, length);
                                }

                                inputStream.close();
                                outputStream.close();

                                // Check if the extracted file is an image
                                if (entryFile.getName().toLowerCase().endsWith(".jpg") || entryFile.getName().toLowerCase().endsWith(".jpeg") ||
                                        entryFile.getName().toLowerCase().endsWith(".png")) {
                                    files.add(entryFile);
                                    ListView.getItems().add(entryFile);
                                }
                            }
                        }

                        zipFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                    files.add(file);
                    ListView.getItems().add(file);
                }
            }

            WaterMarkButton.setDisable(false);
            ResizeButton.setDisable(false);
        }
    }

    @FXML
    void switchToWaterMark(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("WaterMark-view.fxml"));
            Parent root = loader.load();
            WaterMarkComtroller waterMarkController = loader.getController();
            waterMarkController.setImage(files);
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    @FXML
    void switchToResize(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FunctionScene.fxml"));
        Parent root = loader.load();
        ResizeController resizeController = loader.getController();
        resizeController.setImages(files);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        WaterMarkButton.setDisable(true);
        ResizeButton.setDisable(true);
    }
}

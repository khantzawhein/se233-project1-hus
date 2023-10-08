package se233.project_watermark;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import se233.project_watermark.tasks.BatchExportWatermarkTask;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class WaterMarkComtroller implements Initializable {
    @FXML
    private Button MiddleButton;
    @FXML
    private Button LeftButton;
    @FXML
    private Button Next;
    @FXML
    private Button Back;
    @FXML
    private Button ApplyButton;
    @FXML
    private TextField textField;
    @FXML
    private Slider SliderImageQuality;
    @FXML
    private Text TextImageQuality;
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Button saveButton;
    @FXML
    private ImageView imageView;
    @FXML
    private Slider SliderRotation;
    @FXML
    private Slider SliderSize;
    @FXML
    private Slider Slidervisibility;
    @FXML
    private Text TextRotation;
    @FXML
    private Text TextSize;
    @FXML
    private Text TextSlidervisibility;
    private List<Boolean> watermarkAdded = new ArrayList<>();
    private ArrayList<WritableImage> watermarkedImages = new ArrayList<>();
    private List<File> images = new ArrayList<>();
    @FXML
    private ComboBox<String> outputFormat;
    @FXML
    private ProgressBar progressBar;
    private int currentIndex = 0;

    public void backToMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Menu-view.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        watermarkAdded.addAll(Collections.nCopies(images.size(), false));
        this.SliderRotation.valueProperty().addListener((observableValue, number, value) -> {
            int intValue = value.intValue();
            System.out.println(intValue);
        });
        this.SliderSize.valueProperty().addListener((observableValue, number, value) -> {
            int intValue = value.intValue();
            System.out.println(intValue);
        });
        this.Slidervisibility.valueProperty().addListener((observableValue, number, value) -> {
            int intValue = value.intValue();
            System.out.println(intValue);
        });
        this.SliderImageQuality.valueProperty().addListener((observableValue, number, value) -> {
            int intValue = value.intValue();
            System.out.println(intValue);
        });

        this.outputFormat.getItems().addAll("JPEG", "PNG");

        this.outputFormat.selectionModelProperty().get().select(0);
    }

    public void setImage(List<File> files) {
        images.addAll(files);
        watermarkAdded.addAll(Collections.nCopies(files.size(), false));
        watermarkedImages.addAll(Collections.nCopies(files.size(), null));
        updateImageView();
        if (images.size() == 1) {
            Next.setDisable(true);
            Back.setDisable(true);
        }
    }

    @FXML
    private void updateText(MouseEvent event) {
        TextRotation.setText(String.valueOf((int) SliderRotation.getValue()));
        TextSize.setText(String.valueOf((int) SliderSize.getValue()));
        TextSlidervisibility.setText(String.valueOf((int) Slidervisibility.getValue()));
        TextImageQuality.setText(String.valueOf((int) SliderImageQuality.getValue()));
    }

    @FXML
    private void nexImage(ActionEvent event) {
        currentIndex = (currentIndex + 1) % images.size();
        updateImageView();
    }

    @FXML
    private void previousImage(ActionEvent event) {
        currentIndex = (currentIndex - 1 + images.size()) % images.size();
        updateImageView();
    }

    private void updateImageView() {
        if (watermarkedImages.get(currentIndex) != null) {
            imageView.setImage(watermarkedImages.get(currentIndex));
        } else {
            // If watermarked image is not available, use the original image
            Image image = new Image(images.get(currentIndex).toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    private void updateImageWithWaterMark() {
        for (int i = 0; i < images.size(); i++) {
            Image originalImage = new Image(images.get(i).toURI().toString());
            double width = originalImage.getWidth();
            double height = originalImage.getHeight();

            // Create a Canvas with the same dimensions as the image
            Canvas canvas = new Canvas(width, height);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Draw the original image onto the canvas
            gc.drawImage(originalImage, 0, 0);

            // Add watermark text
            gc.setFill(Color.rgb(255, 255, 255, Slidervisibility.getValue() / 100)); // Adjust the alpha value
            Font font = new Font(SliderSize.getValue());
            gc.setFont(font);

            // Define the watermark text
            String watermarkText = textField.getText();

            // Calculate text dimensions
            Text text = new Text(watermarkText);
            text.setFont(font);
            double textWidth = text.getLayoutBounds().getWidth();
            double textHeight = text.getLayoutBounds().getHeight();

            // Calculate the position for centering the watermark
            double x = (width - textWidth) / 2;
            double y = (height - textHeight) / 2;

            // Rotate the graphics context
            double rotation = SliderRotation.getValue();
            gc.save(); // Save the current state
            gc.translate(x + textWidth / 2, y + textHeight / 2); // Translate to the center of the text
            gc.rotate(rotation); // Rotate the graphics context
            gc.fillText(watermarkText, -textWidth / 2, -textHeight / 2); // Draw the text
            gc.restore(); // Restore the saved state

            // Convert the canvas to an image
            WritableImage watermarkedImage = new WritableImage((int) width, (int) height);
            canvas.snapshot(null, watermarkedImage);

            // Add the watermarked image to the list
            watermarkedImages.set(i, watermarkedImage);

            watermarkAdded.set(i, true);
            // Set the watermarked image to the ImageView
        }
        imageView.setImage(watermarkedImages.get(currentIndex));
    }

    @FXML
    private void saveImage(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose a directory to save the watermarked images");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            String outputFormat = this.outputFormat.getSelectionModel().getSelectedItem();
            int quality = (int) SliderImageQuality.getValue();

            BatchExportWatermarkTask batchExportWatermarkTask = new BatchExportWatermarkTask(watermarkedImages, selectedDirectory, outputFormat, quality);
            Thread thread = new Thread(batchExportWatermarkTask);
            thread.start();

            batchExportWatermarkTask.setOnSucceeded(workerStateEvent -> {
                System.out.println("Done");
            });

            progressBar.progressProperty().bind(batchExportWatermarkTask.progressProperty());
        }
    }
}


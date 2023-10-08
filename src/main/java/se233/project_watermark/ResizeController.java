package se233.project_watermark;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import se233.project_watermark.tasks.BatchExportResizeTask;
//import se233.project_watermark.tasks.BatchExportResizeTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ResizeController implements Initializable {

    @FXML
    private ImageView imageView;
    @FXML
    private TextField PercentageTextField;
    @FXML
    private Label labelPercentage;
    @FXML
    private Label percentLabel;
    @FXML
    private TextField WidthTextField;
    @FXML
    private Label widthLabel;
    @FXML
    private Label pixelWidthLabel;
    @FXML
    private TextField HeightTextField;
    @FXML
    private Label heightLabel;
    @FXML
    private Label pixelHeightLabel;
    @FXML
    private Button StartButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button percentageButton;
    @FXML
    private Button widthButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button heightButton;

    private List<File> images = new ArrayList<File>();
    private ArrayList<WritableImage> imageResized = new ArrayList<>();
    private int currentIndex = 0;
    private String selectedOption;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PercentageTextField.setDisable(true);
        PercentageTextField.setVisible(false);
        labelPercentage.setVisible(false);
        percentLabel.setVisible(false);

        WidthTextField.setDisable(true);
        WidthTextField.setVisible(false);
        widthLabel.setVisible(false);
        pixelWidthLabel.setVisible(false);

        HeightTextField.setDisable(true);
        HeightTextField.setVisible(false);
        heightLabel.setVisible(false);
        pixelHeightLabel.setVisible(false);

        selectedOption = null;
    }

    public void setImages(List<File> files) {
        images.addAll(files);
        updateImageView();
    }

    private void updateImageView() {
        if (!images.isEmpty()) {
            File currentImage = images.get(currentIndex);
            Image img = new Image(currentImage.toURI().toString());
            imageView.setImage(img);
        }
    }

    @FXML
    private void selectOption(ActionEvent event) {
        disableAllFields();
        PercentageTextField.clear();
        WidthTextField.clear();
        HeightTextField.clear();

        if (event.getSource() == percentageButton) {
            selectedOption = "Percentage";
            PercentageTextField.setDisable(false);
            PercentageTextField.setVisible(true);
            labelPercentage.setVisible(true);
            percentLabel.setVisible(true);
        } else if (event.getSource() == widthButton) {
            selectedOption = "Width";
            WidthTextField.setDisable(false);
            WidthTextField.setVisible(true);
            widthLabel.setVisible(true);
            pixelWidthLabel.setVisible(true);
        } else if (event.getSource() == heightButton) {
            selectedOption = "Height";
            HeightTextField.setDisable(false);
            HeightTextField.setVisible(true);
            heightLabel.setVisible(true);
            pixelHeightLabel.setVisible(true);
        }
    }

    @FXML
    private void startResize(ActionEvent event) {
        try {
            double percentage = 0;
            double desiredWidth = 0;
            double desiredHeight = 0;

            if (selectedOption != null) {
                switch (selectedOption) {
                    case "Percentage":
                        percentage = Double.parseDouble(PercentageTextField.getText());
                        break;
                    case "Width":
                        desiredWidth = Double.parseDouble(WidthTextField.getText());
                        break;
                    case "Height":
                        desiredHeight = Double.parseDouble(HeightTextField.getText());
                        break;
                }
            }

            for (int i = 0; i < images.size(); i++) {
                {
                    File currentImage = images.get(currentIndex);
                    BufferedImage bufferedImage = ImageIO.read(currentImage);

                    int newWidth, newHeight;

                    if (percentage > 0) {
                        newWidth = (int) (bufferedImage.getWidth() * (percentage / 100));
                        newHeight = (int) (bufferedImage.getHeight() * (percentage / 100));
                    } else if (desiredWidth > 0 && desiredHeight > 0) {
                        newWidth = (int) desiredWidth;
                        newHeight = (int) desiredHeight;
                    } else if (desiredWidth > 0) {
                        newWidth = (int) desiredWidth;
                        newHeight = (int) (bufferedImage.getHeight() * (desiredWidth / bufferedImage.getWidth()));
                    } else if (desiredHeight > 0) {
                        newWidth = (int) (bufferedImage.getWidth() * (desiredHeight / bufferedImage.getHeight()));
                        newHeight = (int) desiredHeight;
                    } else {
                        throw new IllegalArgumentException("Invalid parameters for resizing.");
                    }

                    BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = resizedImage.createGraphics();
                    g.drawImage(bufferedImage, 0, 0, newWidth, newHeight, null);
                    g.dispose();

                    Image fxImage = SwingFXUtils.toFXImage(resizedImage, null);
//                imageView.setImage(fxImage);

                    WritableImage writableImage = new WritableImage(newWidth, newHeight);
                    SwingFXUtils.toFXImage(resizedImage, writableImage);
                    imageResized.add(writableImage);
                    imageView.setImage(writableImage);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disableAllFields() {
        PercentageTextField.setDisable(true);
        PercentageTextField.setVisible(false);
        labelPercentage.setVisible(false);
        percentLabel.setVisible(false);

        WidthTextField.setDisable(true);
        WidthTextField.setVisible(false);
        widthLabel.setVisible(false);
        pixelWidthLabel.setVisible(false);

        HeightTextField.setDisable(true);
        HeightTextField.setVisible(false);
        heightLabel.setVisible(false);
        pixelHeightLabel.setVisible(false);
    }

    @FXML
    private void nextImage() {
        if (!images.isEmpty()) {
            currentIndex = (currentIndex + 1) % images.size();
            updateImageView();
            PercentageTextField.clear();
            WidthTextField.clear();
            HeightTextField.clear();
        }
    }

    @FXML
    private void previousImage() {
        if (!images.isEmpty()) {
            currentIndex = (currentIndex - 1 + images.size()) % images.size();
            updateImageView();
            PercentageTextField.clear();
            WidthTextField.clear();
            HeightTextField.clear();
        }
    }

    @FXML
    private void saveImage(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose a directory to save the watermarked images");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {

            // Assuming you have a List<WritableImage> called imageResized containing resized images
            BatchExportResizeTask batchExportResizeTask = new BatchExportResizeTask(imageResized, selectedDirectory);

            new Thread(batchExportResizeTask).start();

            progressBar.progressProperty().bind(batchExportResizeTask.progressProperty());
        }
    }

}

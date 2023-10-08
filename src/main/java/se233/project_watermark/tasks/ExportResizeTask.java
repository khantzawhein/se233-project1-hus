package se233.project_watermark.tasks;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExportResizeTask extends Task<Void> {
    private List<WritableImage> resizedImages;
    private File outputDirectory;

    public ExportResizeTask(List<WritableImage> resizedImages, File outputDirectory) {
        this.resizedImages = resizedImages;
        this.outputDirectory = outputDirectory;
    }

    @Override
    protected Void call() throws Exception {
        int totalImages = resizedImages.size();

        for (int i = 0; i < totalImages; i++) {
            WritableImage writableImage = resizedImages.get(i);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

            String imageName = "resized_image_" + i + ".png";

            File outputFile = new File(outputDirectory, imageName);

            try {
                ImageIO.write(bufferedImage, "png", outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            updateProgress(i + 1, totalImages);
        }

        return null;
    }
}

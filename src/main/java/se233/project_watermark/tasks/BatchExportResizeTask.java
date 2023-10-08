package se233.project_watermark.tasks;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchExportResizeTask extends Task<Void> {
    private List<WritableImage> resizedImages;
    private File outputDirectory;

    public BatchExportResizeTask(List<WritableImage> resizedImages, File outputDirectory) {
        this.resizedImages = resizedImages;
        this.outputDirectory = outputDirectory;
    }

    @Override
    protected Void call() throws Exception {
        int totalImages = resizedImages.size();

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < totalImages; i++) {
            int finalI = i;
            executorService.execute(() -> {
                WritableImage writableImage = resizedImages.get(finalI);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

                String imageName = "resized_image_" + finalI + ".png";

                File outputFile = new File(outputDirectory, imageName);

                try {
                    ImageIO.write(bufferedImage, "png", outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                updateProgress(finalI + 1, totalImages);
            });
        }

        executorService.close();

        return null;
    }
}

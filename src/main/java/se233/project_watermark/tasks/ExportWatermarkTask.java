package se233.project_watermark.tasks;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public class ExportWatermarkTask implements Callable<Void> {
    private WritableImage imageView;
    private String outputFormat;
    private int quality;
    private int i;
    private File directory;

    public ExportWatermarkTask(WritableImage imageView, File directory, String outputFormat, int quality, int i) {
        this.imageView = imageView;
        this.outputFormat = outputFormat;
        this.quality = quality;
        this.directory = directory;
        this.i = i;
    }
    @Override
    public Void call() {
        try {
            String format = outputFormat;
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageView, null);
            System.out.println(bufferedImage.getWidth() + " " + bufferedImage.getHeight());
            BufferedImage newBufferImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = newBufferImage.createGraphics();

            graphics2D.drawImage(bufferedImage, null, 0, 0);

            File outputFile = new File(Paths.get(directory.getAbsolutePath(), "watermarked-" + i + "." + format.toLowerCase()).toString());
            if (outputFile.exists()) {
                outputFile.delete();
            }
            ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(format.toLowerCase()).next();
            ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            imageWriteParam.setCompressionQuality((this.quality / 100.0f));
            IIOImage iioImage = new IIOImage(newBufferImage, null, null);

            imageWriter.setOutput(ImageIO.createImageOutputStream(outputFile));
            imageWriter.write(null, iioImage, imageWriteParam);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package se233.project_watermark.tasks;

import javafx.concurrent.Task;
import javafx.scene.image.WritableImage;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchExportWatermarkTask extends Task<Void> {
    // 1 Batch Task -> 5 Individual Export Tasks
    private ArrayList<WritableImage> imageViews;
    private String outputFormat;
    private int quality;
    private File directory;
    public BatchExportWatermarkTask(ArrayList<WritableImage> imageViews, File directory, String outputFormat, int quality) {
        this.imageViews = imageViews;
        this.outputFormat = outputFormat;
        this.quality = quality;
        this.directory = directory;
    }

    @Override
    protected Void call() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CompletionService<Void> executorCompletionService = new ExecutorCompletionService<>(executorService);
        System.out.println(imageViews);
        for (int i = 0; i < imageViews.size(); i++) {
            WritableImage imageView = imageViews.get(i);
            ExportWatermarkTask exportWatermarkTask = new ExportWatermarkTask(imageView, directory, outputFormat, quality, i);
            executorCompletionService.submit(exportWatermarkTask);
        }
        for (int j = 0; j < imageViews.size(); j++) {
            executorCompletionService.take();
            updateProgress(j+1, imageViews.size());
        }
        executorService.close();
        return null;
    }
}

package com.example.image_processing.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileActions {

    private final Stage stage;

    public FileActions(Stage stage) {
        this.stage = stage;
    }

    public void openImage(ImageView imageView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(this.stage);
        if (selectedFile != null) {
            try {
                Image image = new Image(new FileInputStream(selectedFile));
                double fitWidth = imageView.getFitWidth();
                double fitHeight = imageView.getFitHeight();
                double imageWidth = image.getWidth();
                double imageHeight = image.getHeight();
                double scaleFactor = Math.min(fitWidth / imageWidth, fitHeight / imageHeight);
                imageView.setImage(image);
                imageView.setFitWidth(scaleFactor * imageWidth);
                imageView.setFitHeight(scaleFactor * imageHeight);

            } catch (FileNotFoundException ex) {
                System.out.println("File not found: " + ex.getMessage());
            }
        }
    }

    public void saveImage(ImageView imageView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(this.stage);
        if (file != null) {
            try {
                double imageWidth = imageView.getBoundsInParent().getWidth();
                double imageHeight = imageView.getBoundsInParent().getHeight();
                WritableImage writableImage = new WritableImage((int) imageWidth, (int) imageHeight);
                imageView.snapshot(null, writableImage);
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            } catch (IOException ex) {
                System.out.println("Error saving image: " + ex.getMessage());
            }
        }
    }
}

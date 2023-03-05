package com.example.image_processing;

import javafx.application.Application;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;

public class HelloApplication extends Application {

    private ImageView imageView;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Image processing");

        Menu fileMenu = new Menu("File");

        MenuItem openBtn = new MenuItem("Open");
        openBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(stage);
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
        });
        fileMenu.getItems().add(openBtn);

        MenuItem saveBtn = new MenuItem("Save");
        saveBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image File");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(stage);
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
        });
        fileMenu.getItems().add(saveBtn);

        Menu editMenu = new Menu("Edit");

        MenuItem undoBtn = new MenuItem("Undo");
        editMenu.getItems().add(undoBtn);

        MenuItem alg1 = new MenuItem("Alg 1");
        editMenu.getItems().add(alg1);

        MenuBar mb = new MenuBar();
        mb.getMenus().add(fileMenu);
        mb.getMenus().add(editMenu);

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        VBox vb = new VBox(mb, imageView);
        Scene scene = new Scene(vb, 750, 500);

        double fitWidth = scene.getWidth() - mb.getHeight();
        double fitHeight = scene.getHeight() - mb.getHeight();
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

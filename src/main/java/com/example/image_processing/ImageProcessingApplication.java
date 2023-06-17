package com.example.image_processing;

import com.example.image_processing.algorithms.HoughTransform;
import com.example.image_processing.algorithms.ImageAlgorithms;
import com.example.image_processing.enums.HistogramType;
import com.example.image_processing.modals.CannyModal;
import com.example.image_processing.modals.HarrisModal;
import com.example.image_processing.utils.FileActions;
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;

public class ImageProcessingApplication extends Application {

    private ImageView imageView;
    private Image originalImage;
    private FileActions fileActions;
    private ImageAlgorithms imageAlgorithms;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Image processing");
        this.fileActions = new FileActions(stage);

        Menu fileMenu = new Menu("File");

        MenuItem openBtn = new MenuItem("Open");
        openBtn.setOnAction(e -> {
            this.fileActions.openImage(imageView);
            this.imageAlgorithms = new ImageAlgorithms(imageView);
            this.originalImage = this.imageView.getImage();
        });
        fileMenu.getItems().add(openBtn);

        MenuItem saveBtn = new MenuItem("Save");
        saveBtn.setOnAction(e -> this.fileActions.saveImage(imageView));
        fileMenu.getItems().add(saveBtn);

        Menu editMenu = new Menu("Edit");

        MenuItem desaturationBtn = new MenuItem("Desaturation");
        desaturationBtn.setOnAction(e -> this.imageAlgorithms.desaturate());
        editMenu.getItems().add(desaturationBtn);

        MenuItem negativeBtn = new MenuItem("Negative");
        negativeBtn.setOnAction(e -> this.imageAlgorithms.createNegative());
        editMenu.getItems().add(negativeBtn);

        MenuItem contrastBrightnessSaturationMenuBtn = new MenuItem("Contrast / Brightness / Saturation");
        Slider brightnessSlider = new Slider(-1.0, 1.0, 0);
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setShowTickMarks(true);
        brightnessSlider.setMajorTickUnit(0.5);
        brightnessSlider.setBlockIncrement(0.1);

        Slider contrastSlider = new Slider(-1.0, 1.0, 0);
        contrastSlider.setShowTickLabels(true);
        contrastSlider.setShowTickMarks(true);
        contrastSlider.setMajorTickUnit(0.5);
        contrastSlider.setBlockIncrement(0.1);

        Slider saturationSlider = new Slider(-1.0, 1.0, 0);
        saturationSlider.setShowTickLabels(true);
        saturationSlider.setShowTickMarks(true);
        saturationSlider.setMajorTickUnit(0.5);
        saturationSlider.setBlockIncrement(0.1);

        contrastBrightnessSaturationMenuBtn.setOnAction(e -> {
            VBox vbox = new VBox();
            vbox.getChildren().addAll(new Label("Contrast"), contrastSlider);
            vbox.getChildren().addAll(new Label("Brightness"), brightnessSlider);
            vbox.getChildren().addAll(new Label("Saturation"), saturationSlider);
            Scene popupScene = new Scene(vbox, 300, 150);
            Stage popupStage = new Stage();
            popupStage.initOwner(stage);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(popupScene);
            popupStage.showAndWait();
        });

        contrastSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double value = newValue.doubleValue();
            this.imageAlgorithms.setContrast(value);
        });
        brightnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double value = newValue.doubleValue();
            this.imageAlgorithms.setBrightness(value);
        });
        saturationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double value = newValue.doubleValue();
            this.imageAlgorithms.setSaturation(value);
        });
        editMenu.getItems().add(contrastBrightnessSaturationMenuBtn);

        Menu histogramMenu = new Menu("Histogram");

        MenuItem redRadioBtn = new MenuItem("RED Histogram");
        redRadioBtn.setOnAction(e -> this.imageAlgorithms.displayHistogram(HistogramType.RED));
        histogramMenu.getItems().add(redRadioBtn);

        MenuItem greenRadioBtn = new MenuItem("GREEN Histogram");
        greenRadioBtn.setOnAction(e -> this.imageAlgorithms.displayHistogram(HistogramType.GREEN));
        histogramMenu.getItems().add(greenRadioBtn);

        MenuItem blueRadioBtn = new MenuItem("BLUE Histogram");
        blueRadioBtn.setOnAction(e -> this.imageAlgorithms.displayHistogram(HistogramType.BLUE));
        histogramMenu.getItems().add(blueRadioBtn);

        MenuItem avgRadioBtn = new MenuItem("AVG Histogram");
        avgRadioBtn.setOnAction(e -> this.imageAlgorithms.displayHistogram(HistogramType.AVERAGE));
        histogramMenu.getItems().add(avgRadioBtn);

        Menu toolsMenu = new Menu("Tools");
        MenuItem thresholdingBtn = new MenuItem("Threshold");
        Slider thresholdingSlider = new Slider(0, 255, 128);
        thresholdingSlider.setShowTickLabels(true);
        thresholdingSlider.setShowTickMarks(true);
        thresholdingSlider.setMajorTickUnit(0.5);
        thresholdingSlider.setBlockIncrement(0.1);
        thresholdingSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double value = newValue.doubleValue();
            this.imageAlgorithms.binarizeImage(value, originalImage);
        });
        thresholdingBtn.setOnAction(e -> {
            VBox vbox = new VBox();
            vbox.getChildren().addAll(new Label("Threshold"), thresholdingSlider);
            Scene popupScene = new Scene(vbox, 300, 150);
            Stage popupStage = new Stage();
            popupStage.initOwner(stage);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(popupScene);
            popupStage.showAndWait();
        });
        toolsMenu.getItems().add(thresholdingBtn);

        MenuItem otsuBtn = new MenuItem("Otsu Algorithm");
        otsuBtn.setOnAction(e -> {
            double otsuValue = this.imageAlgorithms.otsuThreshold(originalImage);
            this.imageAlgorithms.binarizeImage(otsuValue, originalImage);
        });
        toolsMenu.getItems().add(otsuBtn);

        MenuItem cannyBtn = new MenuItem("Canny Edge Detection");
        cannyBtn.setOnAction(e -> {
            CannyModal cannyModal = new CannyModal();
            cannyModal.display(this.imageView, this.originalImage);
        });
        toolsMenu.getItems().add(cannyBtn);

        MenuItem houghBtn = new MenuItem("Hough Transform");
        Slider houghSlider = new Slider(0, 1, 0.5);
        houghSlider.setShowTickLabels(true);
        houghSlider.setShowTickMarks(true);
        houghSlider.setMajorTickUnit(0.5);
        houghSlider.setBlockIncrement(0.1);
        houghSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double thresholdFraction = newValue.doubleValue();
            HoughTransform houghTransform = new HoughTransform(this.originalImage);
            houghTransform.clearAccumulator();
            houghTransform.addPoints(this.originalImage);

            ImageView resultImageView =
                    houghTransform.getLinesImageView(this.originalImage, thresholdFraction);

            this.imageView.setImage(resultImageView.getImage());
        });
        houghBtn.setOnAction(e -> {
            VBox vbox = new VBox();
            vbox.getChildren().addAll(new Label("Threshold value"), houghSlider);
            Scene popupScene = new Scene(vbox, 300, 150);
            Stage popupStage = new Stage();
            popupStage.initOwner(stage);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(popupScene);
            popupStage.showAndWait();
        });
        toolsMenu.getItems().add(houghBtn);

        Menu filterMenu = new Menu("Filters");

        MenuItem averageBlurBtn = new MenuItem("Average Blur");
        averageBlurBtn.setOnAction(e -> this.imageAlgorithms.averageBlur());
        filterMenu.getItems().add(averageBlurBtn);

        MenuItem gaussianBlurBtn = new MenuItem("Gaussian Blur");
        gaussianBlurBtn.setOnAction(e -> this.imageAlgorithms.gaussianBlur());
        filterMenu.getItems().add(gaussianBlurBtn);

        MenuItem sharpenBtn = new MenuItem("Sharpen");
        sharpenBtn.setOnAction(e -> this.imageAlgorithms.sharpen());
        filterMenu.getItems().add(sharpenBtn);

        MenuItem sobelBtn = new MenuItem("Sobel Edge Detection");
        sobelBtn.setOnAction(e -> this.imageAlgorithms.sobelEdgeDetection());
        filterMenu.getItems().add(sobelBtn);

        MenuItem previtBtn = new MenuItem("Previt Edge Detection");
        previtBtn.setOnAction(e -> this.imageAlgorithms.previtEdgeDetection());
        filterMenu.getItems().add(previtBtn);

        MenuItem robertsBtn = new MenuItem("Roberts Edge Detection");
        robertsBtn.setOnAction(e ->this.imageAlgorithms.robertsEdgeDetection());
        filterMenu.getItems().add(robertsBtn);

        Menu operatorsMenu = new Menu("Operators");

        MenuItem dilatationBtn = new MenuItem("Dilatation");
        dilatationBtn.setOnAction(e -> this.imageAlgorithms.dilation());
        operatorsMenu.getItems().add(dilatationBtn);

        MenuItem erosionBtn = new MenuItem("Erosion");
        erosionBtn.setOnAction(e -> this.imageAlgorithms.erosion());
        operatorsMenu.getItems().add(erosionBtn);

        MenuItem hitAndMissBtn = new MenuItem("Hit-And-Miss");
        hitAndMissBtn.setOnAction(e -> this.imageAlgorithms.hitAndMiss());
        operatorsMenu.getItems().add(hitAndMissBtn);

        MenuItem thickenBtn = new MenuItem("Thicken");
        thickenBtn.setOnAction(e -> this.imageAlgorithms.thickening());
        operatorsMenu.getItems().add(thickenBtn);

        MenuItem thinBtn = new MenuItem("Thinning");
        thinBtn.setOnAction(e -> this.imageAlgorithms.thinning());
        operatorsMenu.getItems().add(thinBtn);

        MenuItem harrisBtn = new MenuItem("Harris Corner Detection");
        harrisBtn.setOnAction(e -> new HarrisModal(this.imageView, this.originalImage, this.imageAlgorithms).show());
        toolsMenu.getItems().add(harrisBtn);

        MenuBar mb = new MenuBar();
        mb.getMenus().add(fileMenu);
        mb.getMenus().add(editMenu);
        mb.getMenus().add(histogramMenu);
        mb.getMenus().add(toolsMenu);
        mb.getMenus().add(filterMenu);
        mb.getMenus().add(operatorsMenu);

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

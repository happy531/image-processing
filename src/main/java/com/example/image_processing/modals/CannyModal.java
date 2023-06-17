package com.example.image_processing.modals;

import com.example.image_processing.algorithms.CannyEdgeDetection;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;

public class CannyModal {
    private float gaussianKernelRadius;
    private float lowThreshold;
    private float highThreshold;
    private int gaussianKernelWidth;
    private boolean contrastNormalized;

    public void display(ImageView imageView, Image originalImage) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Canny Edge Detection");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label gaussianKernelRadiusLabel = new Label("Gaussian Kernel Radius:");
        GridPane.setConstraints(gaussianKernelRadiusLabel, 0, 0);

        Slider gaussianKernelRadiusSlider = new Slider(0, 10, 2);
        GridPane.setConstraints(gaussianKernelRadiusSlider, 1, 0);

        Label gaussianKernelRadiusValue = new Label(Double.toString(gaussianKernelRadiusSlider.getValue()));
        GridPane.setConstraints(gaussianKernelRadiusValue, 2, 0);
        gaussianKernelRadiusSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            gaussianKernelRadiusValue.setText(String.format("%.1f", newValue));
        });

        Label lowThresholdLabel = new Label("Low Threshold:");
        GridPane.setConstraints(lowThresholdLabel, 0, 1);

        Slider lowThresholdSlider = new Slider(0, 5, 0.5);
        GridPane.setConstraints(lowThresholdSlider, 1, 1);

        Label lowThresholdValue = new Label(Double.toString(lowThresholdSlider.getValue()));
        GridPane.setConstraints(lowThresholdValue, 2, 1);
        lowThresholdSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            lowThresholdValue.setText(String.format("%.1f", newValue));
        });

        Label highThresholdLabel = new Label("High Threshold:");
        GridPane.setConstraints(highThresholdLabel, 0, 2);

        Slider highThresholdSlider = new Slider(0, 10, 1);
        GridPane.setConstraints(highThresholdSlider, 1, 2);

        Label highThresholdValue = new Label(Double.toString(highThresholdSlider.getValue()));
        GridPane.setConstraints(highThresholdValue, 2, 2);
        highThresholdSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            highThresholdValue.setText(String.format("%.1f", newValue));
        });

        Label gaussianKernelWidthLabel = new Label("Gaussian Kernel Width:");
        GridPane.setConstraints(gaussianKernelWidthLabel, 0, 3);

        Slider gaussianKernelWidthSlider = new Slider(0, 20, 16);
        GridPane.setConstraints(gaussianKernelWidthSlider, 1, 3);

        Label gaussianKernelWidthValue = new Label(Double.toString(gaussianKernelWidthSlider.getValue()));
        GridPane.setConstraints(gaussianKernelWidthValue, 2, 3);
        gaussianKernelWidthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            gaussianKernelWidthValue.setText(String.format("%.0f", newValue));
        });

        Label contrastNormalizedLabel = new Label("Contrast Normalized:");
        GridPane.setConstraints(contrastNormalizedLabel, 0, 4);

        CheckBox contrastNormalizedCheckBox = new CheckBox();
        GridPane.setConstraints(contrastNormalizedCheckBox ,1 ,4);

        Button submitButton = new Button("Detect Edges");
        submitButton.setOnAction(e -> {
            gaussianKernelRadius = (float)gaussianKernelRadiusSlider.getValue();
            lowThreshold = (float)lowThresholdSlider.getValue();
            highThreshold = (float)highThresholdSlider.getValue();
            gaussianKernelWidth = (int)gaussianKernelWidthSlider.getValue();
            contrastNormalized = contrastNormalizedCheckBox.isSelected();

            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);
            CannyEdgeDetection cannyEdgeDetection = new CannyEdgeDetection();

            cannyEdgeDetection.setSourceImage(bufferedImage);
            cannyEdgeDetection.process();
            BufferedImage edges = cannyEdgeDetection.getEdgesImage();

            Image edgedImage = SwingFXUtils.toFXImage(edges, null);
            imageView.setImage(edgedImage);

            window.close();
        });
        GridPane.setConstraints(submitButton, 1, 5);

        grid.getChildren().addAll(gaussianKernelRadiusLabel, gaussianKernelRadiusSlider, gaussianKernelRadiusValue,
                lowThresholdLabel, lowThresholdSlider, lowThresholdValue,
                highThresholdLabel, highThresholdSlider, highThresholdValue,
                gaussianKernelWidthLabel, gaussianKernelWidthSlider, gaussianKernelWidthValue,
                contrastNormalizedLabel, contrastNormalizedCheckBox,
                submitButton);

        Scene scene = new Scene(grid);
        window.setScene(scene);
        window.showAndWait();
    }
}



package com.example.image_processing.modals;

import com.example.image_processing.algorithms.CornerDetection;
import com.example.image_processing.algorithms.ImageAlgorithms;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HarrisModal extends Stage {

    private final Slider minDist;
    private final Slider threshold;
    private final ImageView imageView;
    private final Image originalImage;
    private final ImageAlgorithms imageAlgorithms;

    public HarrisModal(ImageView imageView, Image originalImage, ImageAlgorithms imageAlgorithms) {
        this.imageView = imageView;
        this.originalImage = originalImage;
        this.imageAlgorithms = imageAlgorithms;

        setTitle("Harris Corner Detection");

        // Create sliders
        minDist = createSlider("minDist");
        threshold = createSlider("threshold");

        // Create detect button
        Button detectButton = new Button("Detect");
        detectButton.setOnAction(e -> {
            int minDistValue = (int) minDist.getValue();
            int thresholdValue = (int) threshold.getValue();

            float[][] cornerImg = CornerDetection.detectCorner(this.imageAlgorithms.convertImageToFloatArray(this.originalImage), minDistValue, thresholdValue);
            this.imageView.setImage(this.imageAlgorithms.convertFloatArrayToImage(cornerImg));
        });

        // Create layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(minDist, threshold, detectButton);

        Scene scene = new Scene(layout);
        setScene(scene);
    }

    private Slider createSlider(String label) {
        Slider slider = new Slider(0, 100, 0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setBlockIncrement(1);

        VBox sliderLayout = new VBox(5);
        sliderLayout.getChildren().addAll(slider);

        return slider;
    }
}

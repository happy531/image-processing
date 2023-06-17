package com.example.image_processing.algorithms;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ConvolutionFilter {
    private double[][] kernel;

    public ConvolutionFilter(double[][] kernel) {
        this.kernel = kernel;
    }

    public Image applyFilter(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(width, height);
        PixelReader pixelReader = inputImage.getPixelReader();
        PixelWriter pixelWriter = outputImage.getPixelWriter();

        int kernelWidth = kernel[0].length;
        int kernelHeight = kernel.length;
        int kernelOffsetX = kernelWidth / 2;
        int kernelOffsetY = kernelHeight / 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double red = 0.0;
                double green = 0.0;
                double blue = 0.0;

                for (int ky = 0; ky < kernelHeight; ky++) {
                    for (int kx = 0; kx < kernelWidth; kx++) {
                        int imageX = (int) clamp(x + (kx - kernelOffsetX), 0, width - 1);
                        int imageY = (int) clamp(y + (ky - kernelOffsetY), 0, height - 1);
                        Color color = pixelReader.getColor(imageX, imageY);

                        double kernelValue = kernel[ky][kx];
                        red += color.getRed() * kernelValue;
                        green += color.getGreen() * kernelValue;
                        blue += color.getBlue() * kernelValue;
                    }
                }

                Color newColor = Color.color(clamp(red, 0.0, 1.0), clamp(green, 0.0, 1.0), clamp(blue, 0.0, 1.0));
                pixelWriter.setColor(x, y, newColor);
            }
        }

        return outputImage;
    }

    private double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
}

package com.example.image_processing.algorithms;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class HarrisCornerDetection {
    private final Image originalImage;
    private final double threshold;

    public HarrisCornerDetection(Image image, double threshold) {
        this.originalImage = image;
        this.threshold = threshold;
    }

    public Image process() {
        // Convert JavaFX Image to BufferedImage
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);

        // Convert BufferedImage to grayscale
        BufferedImage grayImage = convertToGrayscale(bufferedImage);

        // Apply Harris Corner Detection algorithm
        BufferedImage cornerImage = detectCorners(grayImage);

        // Mark corners on the original image
        Image processedImage = markCorners(cornerImage);

        return processedImage;
    }

    private BufferedImage convertToGrayscale(BufferedImage image) {
        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        grayImage.getGraphics().drawImage(image, 0, 0, null);
        return grayImage;
    }

    private BufferedImage detectCorners(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] sobelX = new int[width][height];
        int[][] sobelY = new int[width][height];

        // Apply Sobel filter for edge detection
        // ...

        int[][] cornerStrength = new int[width][height];

        // Compute the corner strength using Harris Corner Detection formula
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gradientX = sobelX[x][y];
                int gradientY = sobelY[x][y];

                // Compute components of the structure tensor
                int sumXX = gradientX * gradientX;
                int sumYY = gradientY * gradientY;
                int sumXY = gradientX * gradientY;

                // Apply Gaussian smoothing to the structure tensor components
                int smoothingRadius = 3;
                for (int j = -smoothingRadius; j <= smoothingRadius; j++) {
                    for (int i = -smoothingRadius; i <= smoothingRadius; i++) {
                        int neighborX = x + i;
                        int neighborY = y + j;
                        if (neighborX >= 0 && neighborX < width && neighborY >= 0 && neighborY < height) {
                            int gradientXNeighbor = sobelX[neighborX][neighborY];
                            int gradientYNeighbor = sobelY[neighborX][neighborY];

                            sumXX += gradientXNeighbor * gradientXNeighbor;
                            sumYY += gradientYNeighbor * gradientYNeighbor;
                            sumXY += gradientXNeighbor * gradientYNeighbor;
                        }
                    }
                }

                // Compute the corner response function value
                double k = 0.04; // Empirical constant
                double det = (sumXX * sumYY) - (sumXY * sumXY);
                double trace = sumXX + sumYY;
                double cornerResponse = det - k * (trace * trace);

                cornerStrength[x][y] = (int) Math.round(cornerResponse);
            }
        }

        // Find local maxima as corner points above the threshold
        BufferedImage cornerImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int cornerResponse = cornerStrength[x][y];
                if (cornerResponse > threshold && isLocalMaximum(cornerStrength, x, y)) {
                    int red = (int) (Color.WHITE.getRed() * 255);
                    int green = (int) (Color.WHITE.getGreen() * 255);
                    int blue = (int) (Color.WHITE.getBlue() * 255);
                    int rgb = (red << 16) | (green << 8) | blue;
                    cornerImage.setRGB(x, y, rgb);
                }
            }
        }

        return cornerImage;
    }

    private boolean isLocalMaximum(int[][] cornerStrength, int x, int y) {
        int centerValue = cornerStrength[x][y];

        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                if (cornerStrength[x + i][y + j] >= centerValue) {
                    return false;
                }
            }
        }

        return true;
    }

    private Image markCorners(BufferedImage image) {
        WritableImage writableImage = new WritableImage(image.getWidth(), image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        WritableRaster raster = image.getRaster();
        int[] pixelBuffer = new int[1];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                raster.getPixel(x, y, pixelBuffer);

                int cornerResponse = pixelBuffer[0];
                if (cornerResponse > threshold) {
                    // Set pixel color to red for corners above the threshold
                    pixelWriter.setColor(x, y, Color.rgb(200, 200, 200));
                } else {
                    // Set pixel color to original image color
                    int grayValue = pixelBuffer[0];
                    pixelWriter.setColor(x, y, Color.rgb(grayValue, grayValue, grayValue));
                }
            }
        }

        return writableImage;
    }


}

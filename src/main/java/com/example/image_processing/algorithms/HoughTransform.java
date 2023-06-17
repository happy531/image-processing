package com.example.image_processing.algorithms;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class HoughTransform {
    private final int width;
    private final int height;
    private int[][] accumulator;
    private int maxAccumulator;

    public HoughTransform(Image image) {
        this.width = (int) image.getWidth();
        this.height = (int) image.getHeight();
        this.accumulator = new int[180][height];
        this.maxAccumulator = 0;
    }

    public void clearAccumulator() {
        this.accumulator = new int[180][height];
        this.maxAccumulator = 0;
    }

    public void addPoints(Image image) {
        PixelReader pixelReader = image.getPixelReader();
        IntStream.range(0, width).forEach(x -> IntStream.range(0, height).forEach(y -> {
            Color pixelColor = pixelReader.getColor(x, y);
            if (pixelColor.equals(Color.BLACK)) {
                addPoint(x, y);
            }
        }));
    }

    public void addPoint(int x, int y) {
        for (int theta = 0; theta < 180; theta++) {
            double radians = Math.toRadians(theta);
            int rho = (int) (x * Math.cos(radians) + y * Math.sin(radians));
            if (rho >= 0 && rho < height) {
                accumulator[theta][rho]++;
                if (accumulator[theta][rho] > maxAccumulator) {
                    maxAccumulator = accumulator[theta][rho];
                }
            }
        }
    }

    public ImageView getLinesImageView(Image image, double thresholdFraction) {
        List<Line> lines = new ArrayList<>();

        int threshold = (int) (maxAccumulator * thresholdFraction);

        for (int theta = 0; theta < 180; theta++) {
            for (int rho = 0; rho < height; rho++) {
                if (accumulator[theta][rho] > threshold) {
                    double radians = Math.toRadians(theta);
                    double a = Math.cos(radians);
                    double b = Math.sin(radians);
                    double x0 = a * rho;
                    double y0 = b * rho;
                    int x1 = (int) (x0 + 1000 * (-b));
                    int y1 = (int) (y0 + 1000 * a);
                    int x2 = (int) (x0 - 1000 * (-b));
                    int y2 = (int) (y0 - 1000 * a);
                    lines.add(new Line(x1, y1, x2, y2));
                }
            }
        }

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage(image, 0, 0);
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);

        for (Line line : lines) {
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        }

        BufferedImage bufferedImage =
                SwingFXUtils.fromFXImage(canvas.snapshot(null, null), null);
        Image resultImage =
                SwingFXUtils.toFXImage(bufferedImage, null);

        return new ImageView(resultImage);
    }
}

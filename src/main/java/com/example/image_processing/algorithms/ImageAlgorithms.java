package com.example.image_processing.algorithms;

import com.example.image_processing.algorithms.ConvolutionFilter;
import com.example.image_processing.enums.HistogramType;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ImageAlgorithms {
    private final ImageView imageView;

    public ImageAlgorithms(ImageView imageView) {
        this.imageView = imageView;
    }

    public void createNegative() {
        Image image = imageView.getImage();
        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage negativeImage = new WritableImage(width, height);
        PixelWriter pixelWriter = negativeImage.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = pixelReader.getColor(x, y);
                double red = 255 - color.getRed() * 255;
                double green = 255 - color.getGreen() * 255;
                double blue = 255 - color.getBlue() * 255;
                Color negativeColor = Color.color(red / 255, green / 255, blue / 255);
                pixelWriter.setColor(x, y, negativeColor);
            }
        }
        imageView.setImage(negativeImage);
    }

    public void desaturate() {
        Image image = imageView.getImage();
        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage grayImage = new WritableImage(width, height);
        PixelWriter pixelWriter = grayImage.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = pixelReader.getColor(x, y);
                double gray = 0.2989 * color.getRed() + 0.5870 * color.getGreen() + 0.1140 * color.getBlue();
                Color grayColor = Color.color(gray, gray, gray);
                pixelWriter.setColor(x, y, grayColor);
            }
        }
        imageView.setImage(grayImage);
    }

    public void setBrightness(double value) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(value);
        imageView.setEffect(colorAdjust);
    }

    public void setContrast(double value) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(value);
        imageView.setEffect(colorAdjust);
    }

    public void setSaturation(double value) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(value);
        imageView.setEffect(colorAdjust);
    }

    public void averageBlur () {
        double[][] kernel = {
                {1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0},
                {1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0},
                {1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0}
        };

        ConvolutionFilter filter = new ConvolutionFilter(kernel);
        Image filteredImage = filter.applyFilter(imageView.getImage());
        imageView.setImage(filteredImage);
    }

    public void gaussianBlur() {
        double[][] kernel = {
                {1.0 / 16.0, 1.0 / 8.0, 1.0 / 16.0},
                {1.0 / 8.0, 1.0 / 4.0, 1.0 / 8.0},
                {1.0 / 16.0, 1.0 / 8.0, 1.0 / 16.0}
        };

        ConvolutionFilter filter = new ConvolutionFilter(kernel);
        Image filteredImage = filter.applyFilter(imageView.getImage());
        imageView.setImage(filteredImage);
    }

    public void sharpen() {
        double[][] kernel = {
                {0.0, -1.0, 0.0},
                {-1.0, 5.0, -1.0},
                {0.0, -1.0, 0.0}
        };

        ConvolutionFilter filter = new ConvolutionFilter(kernel);
        Image filteredImage = filter.applyFilter(imageView.getImage());
        imageView.setImage(filteredImage);
    }

    public void sobelEdgeDetection() {
        double[][] kernelX = {
                {-1.0, 0.0, 1.0},
                {-2.0, 0.0, 2.0},
                {-1.0, 0.0, 1.0}
        };

        double[][] kernelY = {
                {-1.0, -2.0, -1.0},
                {0.0, 0.0, 0.0},
                {1.0, 2.0, 1.0}
        };

        ConvolutionFilter filterX = new ConvolutionFilter(kernelX);
        ConvolutionFilter filterY = new ConvolutionFilter(kernelY);

        Image imageX = filterX.applyFilter(imageView.getImage());
        Image imageY = filterY.applyFilter(imageView.getImage());

        WritableImage outputImage = new WritableImage(imageX.getPixelReader(), (int) imageX.getWidth(), (int) imageX.getHeight());

        PixelReader readerX = imageX.getPixelReader();
        PixelReader readerY = imageY.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        int width = (int) imageX.getWidth();
        int height = (int) imageX.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color colorX = readerX.getColor(x, y);
                Color colorY = readerY.getColor(x, y);

                double grayX = (colorX.getRed() + colorX.getGreen() + colorX.getBlue()) / 3.0;
                double grayY = (colorY.getRed() + colorY.getGreen() + colorY.getBlue()) / 3.0;

                double magnitude = Math.sqrt(grayX * grayX + grayY * grayY);
                int grayScale = (int) (magnitude * 255.0 / Math.sqrt(2.0));
                Color newColor = Color.rgb(grayScale, grayScale, grayScale);

                writer.setColor(x, y, newColor);
            }
        }

        imageView.setImage(outputImage);
    }

    public void previtEdgeDetection() {
        double[][] kernelX = {
                {-1.0, 0.0, 1.0},
                {-1.0, 0.0, 1.0},
                {-1.0, 0.0, 1.0}
        };

        double[][] kernelY = {
                {-1.0, -1.0, -1.0},
                {0.0, 0.0, 0.0},
                {1.0, 1.0, 1.0}
        };

        ConvolutionFilter filterX = new ConvolutionFilter(kernelX);
        ConvolutionFilter filterY = new ConvolutionFilter(kernelY);

        Image imageX = filterX.applyFilter(imageView.getImage());
        Image imageY = filterY.applyFilter(imageView.getImage());

        WritableImage outputImage = new WritableImage(imageX.getPixelReader(), (int) imageX.getWidth(), (int) imageX.getHeight());

        PixelReader readerX = imageX.getPixelReader();
        PixelReader readerY = imageY.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        int width = (int) imageX.getWidth();
        int height = (int) imageX.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color colorX = readerX.getColor(x, y);
                Color colorY = readerY.getColor(x, y);

                double grayX = (colorX.getRed() + colorX.getGreen() + colorX.getBlue()) / 3.0;
                double grayY = (colorY.getRed() + colorY.getGreen() + colorY.getBlue()) / 3.0;

                double magnitude = Math.sqrt(grayX * grayX + grayY * grayY);
                int grayScale = (int) (magnitude * 255.0 / Math.sqrt(2.0));
                Color newColor = Color.rgb(grayScale, grayScale, grayScale);

                writer.setColor(x, y, newColor);
            }
        }

        imageView.setImage(outputImage);
    }

    public void robertsEdgeDetection() {
        double[][] kernelX = {
                {1.0, 0.0},
                {0.0, -1.0}
        };

        double[][] kernelY = {
                {0.0, 1.0},
                {-1.0, 0.0}
        };

        ConvolutionFilter filterX = new ConvolutionFilter(kernelX);
        ConvolutionFilter filterY = new ConvolutionFilter(kernelY);

        Image imageX = filterX.applyFilter(imageView.getImage());
        Image imageY = filterY.applyFilter(imageView.getImage());

        WritableImage outputImage = new WritableImage(imageX.getPixelReader(), (int) imageX.getWidth(), (int) imageX.getHeight());

        PixelReader readerX = imageX.getPixelReader();
        PixelReader readerY = imageY.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        int width = (int) imageX.getWidth();
        int height = (int) imageX.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color colorX = readerX.getColor(x, y);
                Color colorY = readerY.getColor(x, y);

                double grayX = (colorX.getRed() + colorX.getGreen() + colorX.getBlue()) / 3.0;
                double grayY = (colorY.getRed() + colorY.getGreen() + colorY.getBlue()) / 3.0;

                double magnitude = Math.sqrt(grayX * grayX + grayY * grayY);
                int grayScale = (int) (magnitude * 255.0 / Math.sqrt(2.0));
                Color newColor = Color.rgb(grayScale, grayScale, grayScale);

                writer.setColor(x, y, newColor);
            }
        }

        imageView.setImage(outputImage);
    }

    public void laplaceEdgeDetection() {
        double[][] kernel = {
                {0.0, 1.0, 0.0},
                {1.0, -4.0, 1.0},
                {0.0, 1.0, 0.0}
        };

        ConvolutionFilter filter = new ConvolutionFilter(kernel);
        Image filteredImage = filter.applyFilter(imageView.getImage());

        WritableImage outputImage = new WritableImage(filteredImage.getPixelReader(),
                (int) filteredImage.getWidth(), (int) filteredImage.getHeight());

        PixelReader reader = filteredImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        int width = (int) filteredImage.getWidth();
        int height = (int) filteredImage.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                double intensity = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;

                int grayScale = (int) (intensity * 255.0);
                Color newColor = Color.rgb(grayScale, grayScale, grayScale);

                writer.setColor(x, y, newColor);
            }
        }

        imageView.setImage(outputImage);
    }

    public void LoGEdgeDetection() {
        double sigma = 1.4;
        int size = 5;

        double[][] kernel = new double[size][size];
        double sum = 0.0;

        // create LoG kernel
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double value = (x - (double) size / 2) * (x - (double) size / 2) + (y - (double) size / 2) * (y - (double) size / 2);
                kernel[x][y] = (value - 2.0 * sigma * sigma) / (sigma * sigma * sigma * sigma) * Math.exp(-value / (2.0 * sigma * sigma));
                sum += kernel[x][y];
            }
        }

        // normalize kernel
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                kernel[x][y] /= sum;
            }
        }

        ConvolutionFilter filter = new ConvolutionFilter(kernel);
        Image filteredImage = filter.applyFilter(imageView.getImage());

        WritableImage outputImage = new WritableImage(filteredImage.getPixelReader(), (int) filteredImage.getWidth(), (int) filteredImage.getHeight());
        PixelReader reader = filteredImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        int width = (int) filteredImage.getWidth();
        int height = (int) filteredImage.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                double gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
                int grayScale = (int) ((gray + 1.0) * 127.5);
                Color newColor = Color.rgb(grayScale, grayScale, grayScale);
                writer.setColor(x, y, newColor);
            }
        }

        imageView.setImage(outputImage);
    }

    public void displayHistogram(HistogramType histogramType) {
        String chartTitle = "HISTOGRAM";
        Image image = imageView.getImage();
        int[] histogramData = new int[256];
        PixelReader pixelReader = image.getPixelReader();

        switch (histogramType) {
            case RED -> IntStream.range(0, (int) image.getWidth())
                    .parallel()
                    .forEach(x -> IntStream.range(0, (int) image.getHeight())
                            .parallel()
                            .forEach(y -> {
                                Color color = pixelReader.getColor(x, y);
                                int redValue = (int) (color.getRed() * 255);
                                histogramData[redValue]++;
                            }));
            case GREEN -> IntStream.range(0, (int) image.getWidth())
                    .parallel()
                    .forEach(x -> IntStream.range(0, (int) image.getHeight())
                            .parallel()
                            .forEach(y -> {
                                Color color = pixelReader.getColor(x, y);
                                int greenValue = (int) (color.getGreen() * 255);
                                histogramData[greenValue]++;
                            }));
            case BLUE -> IntStream.range(0, (int) image.getWidth())
                    .parallel()
                    .forEach(x -> IntStream.range(0, (int) image.getHeight())
                            .parallel()
                            .forEach(y -> {
                                Color color = pixelReader.getColor(x, y);
                                int blueValue = (int) (color.getBlue() * 255);
                                histogramData[blueValue]++;
                            }));
            case AVERAGE -> IntStream.range(0, (int) image.getWidth())
                    .parallel()
                    .forEach(x -> IntStream.range(0, (int) image.getHeight())
                            .parallel()
                            .forEach(y -> {
                                Color color = pixelReader.getColor(x, y);
                                int grayValue = (int) ((color.getRed() + color.getGreen() + color.getBlue()) / 3 * 255);
                                histogramData[grayValue]++;
                            }));
            default -> {
                return;
            }
        }

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(chartTitle);
        xAxis.setLabel("Pixel Value");
        yAxis.setLabel("Frequency");
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();

        IntStream.range(0, histogramData.length)
                .forEach(i -> {
                    String pixelValue = String.valueOf(i);
                    dataSeries.getData().add(new XYChart.Data<>(pixelValue, histogramData[i]));
                });
        chart.getData().add(dataSeries);

        Scene chartScene = new Scene(chart, 600, 400);
        Stage chartStage = new Stage();
        chartStage.setScene(chartScene);
        chartStage.show();
    }

    public void binarizeImage(double threshold, Image originalImage) {
        Image binarizedImage = imageView.getImage();
        PixelReader originalPixelReader = originalImage.getPixelReader();
        int width = (int) binarizedImage.getWidth();
        int height = (int) binarizedImage.getHeight();
        WritableImage newImage = new WritableImage(width, height);
        PixelWriter pixelWriter = newImage.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color black = Color.BLACK;
                Color white = Color.WHITE;
                Color color = originalPixelReader.getColor(x, y);
                if (color.getBrightness() > threshold / 255.0) {
                    pixelWriter.setColor(x, y, black);
                }
                else {
                    pixelWriter.setColor(x, y, white);
                }
            }
        }
        imageView.setImage(newImage);
    }

    public int otsuThreshold(Image image) {
        int[] histogram = imageHistogram(image);
        double total = image.getWidth() * image.getHeight();

        float sum = 0;
        for (int i = 0; i < 256; i++) sum += i * histogram[i];

        float sumB = 0;
        int wB = 0;
        double wF;

        double varMax = 0;
        int threshold = 0;

        for (int i = 0; i < 256; i++) {
            wB += histogram[i];
            if (wB == 0) continue;
            wF = total - wB;
            if (wF == 0) break;

            sumB += (float) (i * histogram[i]);
            float mB = sumB / wB;
            double mF = (sum - sumB) / wF;

            double varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }

        return threshold;
    }

    public static int[] imageHistogram(Image input) {
        int[] histogram = new int[256];

        PixelReader pixelReader = input.getPixelReader();
        int width = (int) input.getWidth();
        int height = (int) input.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = pixelReader.getColor(x, y);
                int red = (int) (color.getRed() * 255);
                histogram[red]++;
            }
        }

        return histogram;
    }

    public void erosion() {
        Image image = imageView.getImage();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage dilatedImage = new WritableImage(width, height);
        PixelWriter pixelWriter = dilatedImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color maxColor = Color.BLACK;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int newX = x + i;
                        int newY = y + j;
                        if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                            Color neighborColor = image.getPixelReader().getColor(newX, newY);
                            maxColor = Color.color(
                                    Math.max(maxColor.getRed(), neighborColor.getRed()),
                                    Math.max(maxColor.getGreen(), neighborColor.getGreen()),
                                    Math.max(maxColor.getBlue(), neighborColor.getBlue())
                            );
                        }
                    }
                }
                pixelWriter.setColor(x, y, maxColor);
            }
        }
        imageView.setImage(dilatedImage);
    }

    public void dilation() {
        Image image = imageView.getImage();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage erodedImage = new WritableImage(width, height);
        PixelWriter pixelWriter = erodedImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color minColor = Color.WHITE;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int newX = x + i;
                        int newY = y + j;
                        if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                            Color neighborColor = image.getPixelReader().getColor(newX, newY);
                            minColor = Color.color(
                                    Math.min(minColor.getRed(), neighborColor.getRed()),
                                    Math.min(minColor.getGreen(), neighborColor.getGreen()),
                                    Math.min(minColor.getBlue(), neighborColor.getBlue())
                            );
                        }
                    }
                }
                pixelWriter.setColor(x, y, minColor);
            }
        }
        imageView.setImage(erodedImage);
    }

    public void hitAndMiss() {
        Image image = imageView.getImage();
        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage outputImage = new WritableImage(width, height);
        PixelWriter pixelWriter = outputImage.getPixelWriter();

        int[][] hitMatrix = {
                {0, 0, 0},
                {-1, 1, 0},
                {1, 1, 1}
        };

        int[][] missMatrix = {
                {1, 0, 1},
                {0, -1, 0},
                {0, 0, 0}
        };

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                List<Integer> neighbors = getNeighbors(x, y, width, height);
                boolean hit = true;
                boolean miss = true;

                for (int i = 0; i < neighbors.size(); i++) {
                    int neighbor = neighbors.get(i);
                    int hitValue = hitMatrix[i / 3][i % 3];
                    int missValue = missMatrix[i / 3][i % 3];

                    if (hitValue != 0 && hitValue != neighbor) {
                        hit = false;
                    }

                    if (missValue != 0 && missValue != neighbor) {
                        miss = false;
                    }
                }

                if (hit || miss) {
                    pixelWriter.setColor(x, y, Color.BLACK);
                } else {
                    pixelWriter.setColor(x, y, Color.WHITE);
                }
            }
        }

        imageView.setImage(outputImage);
    }

    public void thickening() {
        Image image = imageView.getImage();
        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage thickenedImage = new WritableImage(width, height);
        PixelWriter pixelWriter = thickenedImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = pixelReader.getColor(x, y);
                if (color.equals(Color.BLACK)) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int newX = x + dx;
                            int newY = y + dy;
                            if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                                pixelWriter.setColor(newX, newY, Color.BLACK);
                            }
                        }
                    }
                } else {
                    pixelWriter.setColor(x, y, Color.WHITE);
                }
            }
        }

        imageView.setImage(thickenedImage);
    }

    private List<Integer> getNeighbors(int x, int y, int width, int height) {
        List<Integer> neighbors = new ArrayList<>();

        for (int yOffset = -1; yOffset <= 1; yOffset++) {
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                int neighborX = x + xOffset;
                int neighborY = y + yOffset;

                if (neighborX >= 0 && neighborX < width && neighborY >= 0 && neighborY < height) {
                    Color neighborColor = imageView.getImage().getPixelReader().getColor(neighborX, neighborY);
                    int neighborValue = neighborColor.equals(Color.BLACK) ? 1 : 0;
                    neighbors.add(neighborValue);
                } else {
                    neighbors.add(0);
                }
            }
        }

        return neighbors;
    }

    public void thinning() {
        Image image = imageView.getImage();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage thinnedImage = new WritableImage(width, height);
        PixelWriter pixelWriter = thinnedImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = image.getPixelReader().getColor(x, y);
                if (color.equals(Color.BLACK)) {
                    pixelWriter.setColor(x, y, Color.BLACK);
                    boolean shouldErase = true;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int newX = x + i;
                            int newY = y + j;
                            if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                                Color neighborColor = image.getPixelReader().getColor(newX, newY);
                                if (neighborColor.equals(Color.WHITE)) {
                                    shouldErase = false;
                                }
                            }
                        }
                    }
                    if (shouldErase) {
                        pixelWriter.setColor(x, y, Color.WHITE);
                    }
                } else {
                    pixelWriter.setColor(x, y, Color.WHITE);
                }
            }
        }
        imageView.setImage(thinnedImage);
    }

    public float[][] convertImageToFloatArray(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Create a buffer to store the pixel values
        byte[] buffer = new byte[width * height * 4];

        // Read the pixel values into the buffer
        PixelReader pixelReader = image.getPixelReader();
        pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getByteBgraInstance(), buffer, 0, width * 4);

        // Convert the pixel values to float[][] representation
        float[][] floatArray = new float[height][width];
        int bufferIndex = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = buffer[bufferIndex++] & 0xff;
                int g = buffer[bufferIndex++] & 0xff;
                int b = buffer[bufferIndex++] & 0xff;
                int a = buffer[bufferIndex++] & 0xff;
                floatArray[y][x] = (float) ((float) ((r + g + b) / 3.0) / 255.0);
            }
        }

        return floatArray;
    }

    public Image convertFloatArrayToImage(float[][] floatArray) {
        int width = floatArray[0].length;
        int height = floatArray.length;

        // Create a new writable image
        WritableImage image = new WritableImage(width, height);

        // Get the pixel writer
        PixelWriter pixelWriter = image.getPixelWriter();

        // Convert the float[][] array to Image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Convert the float value to a Color object
                Color color = new Color(floatArray[y][x], floatArray[y][x], floatArray[y][x], 1.0);

                // Write the color to the image
                pixelWriter.setColor(x, y, color);
            }
        }

        return image;
    }

}

package com.example.image_processing.algorithms;

public class CornerDetection {
    public static float[][] detectCorner(float[][] img, int minDist, int threshold) {
        int height = img.length;
        int width = img[0].length;
        float[][] result = new float[height][width];
        for (int i = 0; i < result.length; i++) {
            System.arraycopy(img[i], 0, result[i], 0, result[0].length);
        }

        float[][] gray = reduceMatrix(turnGrayscale(result));

        float[][] sobelXX = sobelFilteringGetX(sobelFilteringGetX(gray));
        float[][] sobelXY = sobelFilteringGetY(sobelFilteringGetX(gray));
        float[][] sobelYY = sobelFilteringGetY(sobelFilteringGetY(gray));

        float[][] det = new float[sobelXX.length][sobelXX[0].length];
        float[][] trace = new float[sobelXX.length][sobelXX[0].length];
        float[][] harris = new float[sobelXX.length][sobelXX[0].length];
        for (int i = minDist; i < sobelXX.length - minDist; i++) {
            for (int j = minDist; j < sobelXX[0].length - minDist; j++) {
                for (int x = -minDist; x < minDist; x++) {
                    for (int y = -minDist; y < minDist; y++) {
                        det[i][j] += (sobelXX[i + x][j + y] * sobelYY[i + x][j + y]) - (sobelXY[i + x][j + y] * sobelXY[i + x][j + y]);
                        trace[i][j] += sobelXX[i + x][j + y] + sobelYY[i + x][j + y];
                        harris[i][j] += (float) (det[i + x][j + y] - 0.2 * trace[i + x][j + y]);
                    }
                }
            }
        }
        normaliseMatrix(harris);
        int posX;
        int posY1;
        int posY2;
        int posY3;
        for (int i = 0; i < sobelXX.length; i++) {
            for (int j = 0; j < sobelXX[0].length; j++) {
                if (harris[i][j] > threshold) {
                    for (int x = -5; x < 5; x++) {
                        for (int y = -5; y < 5; y++) {
                            posX = i + x;
                            posY1 = ((j + y) * 3);
                            posY2 = ((j + y) * 3) + 1;
                            posY3 = ((j + y) * 3) + 2;
                            if ((posX >= 0 && posX < result.length) && (posY1 >= 0 && posY3 < result[0].length)) {
                                result[posX][posY1] = 1;
                                result[posX][posY2] = 0.34f;
                                result[posX][posY3] = 0.2f;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    static void normaliseMatrix(float[][] matrix) {
        float max = 0;
        for (float[] floats : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (Math.abs(floats[j]) > max) {
                    max = Math.abs(floats[j]);
                }
            }
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] /= max;
                matrix[i][j] *= 100;
            }
        }
    }

    static float[][] sobelFilteringGetX(float[][] image) {
        int height = image.length;
        int width = image[0].length;
        float[][] filteredImage = new float[height][width];

        // pre-compute pixel weights for each neighbor
        float[][] sobelX = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float pixel = 0.f;
                float neighbor1 = image[y - 1][x - 1];
                float neighbor2 = image[y - 1][x];
                float neighbor3 = image[y - 1][x + 1];
                float neighbor4 = image[y][x - 1];
                float neighbor6 = image[y][x + 1];
                float neighbor7 = image[y + 1][x - 1];
                float neighbor8 = image[y + 1][x];
                float neighbor9 = image[y + 1][x + 1];
                pixel += neighbor1 * sobelX[0][0];
                pixel += neighbor2 * sobelX[0][1];
                pixel += neighbor3 * sobelX[0][2];
                pixel += neighbor4 * sobelX[1][0];
                pixel += neighbor6 * sobelX[1][2];
                pixel += neighbor7 * sobelX[2][0];
                pixel += neighbor8 * sobelX[2][1];
                pixel += neighbor9 * sobelX[2][2];
                filteredImage[y][x] = pixel;
            }
        }
        return filteredImage;
    }

    static float[][] sobelFilteringGetY(float[][] image) {
        int height = image.length;
        int width = image[0].length;

        float[][] filteredImage = new float[height][width];

        // macierze
        float[][] sobelY = {
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        };

        // starterPoint is always 1 for Sobel
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float pixel = 0.f;
                float neighbor1 = image[y - 1][x - 1];
                float neighbor2 = image[y - 1][x];
                float neighbor3 = image[y - 1][x + 1];
                float neighbor4 = image[y][x - 1];
                float neighbor6 = image[y][x + 1];
                float neighbor7 = image[y + 1][x - 1];
                float neighbor8 = image[y + 1][x];
                float neighbor9 = image[y + 1][x + 1];
                pixel += neighbor1 * sobelY[0][0];
                pixel += neighbor2 * sobelY[0][1];
                pixel += neighbor3 * sobelY[0][2];
                pixel += neighbor4 * sobelY[1][0];
                pixel += neighbor6 * sobelY[1][2];
                pixel += neighbor7 * sobelY[2][0];
                pixel += neighbor8 * sobelY[2][1];
                pixel += neighbor9 * sobelY[2][2];
                filteredImage[y][x] = pixel;
            }
        }

        return filteredImage;
    }

    static float[][] turnGrayscale(float[][] image) {
        float[][] result = new float[image.length][image[0].length];
        for (int x = 0; x < image.length; x++) {
            for (int y = 0; y < image[0].length / 3; y++) {
                float r = image[x][y * 3];
                float g = image[x][y * 3 + 1];
                float b = image[x][y * 3 + 2];
                float avg = (float) (0.299 * r + 0.587 * g + 0.114 * b);
                result[x][y * 3] = avg;
                result[x][y * 3 + 1] = avg;
                result[x][y * 3 + 2] = avg;
            }
        }
        return result;
    }

    static float[][] reduceMatrix(float[][] image) {
        int height = image.length;
        int width = image[0].length / 3;

        float[][] result = new float[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result[i][j] = image[i][j * 3];
            }
        }

        return result;
    }
}

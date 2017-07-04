package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Utility class for writing height maps to images.
 */
public class ImageWriter {

    private int[][] floatToIntArray(float[][] floatArray) {
        int width = floatArray.length;
        int height = floatArray[0].length;
        int[][] intArray = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                intArray[i][j] = (int) floatArray[i][j];
            }
        }
        return intArray;
    }

    public void writeHeightMapToImage(String path, float[][] rgbData) throws IOException {
        int[][] rgbAsInt = floatToIntArray(rgbData);
        writeHeightMapToImage(path, rgbAsInt);
    }

    public void writeHeightMapToImage(String path, int[][] rgbData) throws IOException {
        final int width = rgbData.length;
        final int height = rgbData[0].length;
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                image.setRGB(x, y, rgbData[x][y]);
            }
        }
        File file = new File(path);
        ImageIO.write(image, "png", file);
    }
}
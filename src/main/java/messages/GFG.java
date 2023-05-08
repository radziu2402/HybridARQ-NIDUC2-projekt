package messages;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GFG {
    public static void main(String[] args) throws IOException {
        // read the image from the file
        BufferedImage image = ImageIO.read(new File("C:\\Users\\radziu2402\\Desktop\\sample.bmp"));

        // create the object of ByteArrayOutputStream class
        ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();

        // write the image into the object of ByteArrayOutputStream class
        ImageIO.write(image, "BMP", outStreamObj);

        // create the byte array from image
        byte[] byteArray = outStreamObj.toByteArray();
        boolean[] booleans = byteArrayToBooleanArray(byteArray);
        byte[] bytes = booleanArrayToByteArray(booleans);

        // create the object of ByteArrayInputStream class
        // and initialized it with the byte array.
        ByteArrayInputStream inStreambj = new ByteArrayInputStream(bytes);
        // read image from byte array
        BufferedImage newImage = ImageIO.read(inStreambj);

        // write output image
        ImageIO.write(newImage, "BMP", new File("outputImage.bmp"));
        System.out.println("Image generated from the byte array.");
    }


    // method to convert byte array to boolean array
    public static boolean[] byteArrayToBooleanArray(byte[] byteArray) {
        boolean[] boolArray = new boolean[byteArray.length * 8];

        for (int i = 0; i < byteArray.length; i++) {
            for (int j = 0; j < 8; j++) {
                boolArray[i * 8 + j] = ((byteArray[i] >> j) & 1) == 1;
            }
        }
        return boolArray;
    }

    // method to convert boolean array to byte array
    public static byte[] booleanArrayToByteArray(boolean[] boolArray) {
        byte[] byteArray = new byte[boolArray.length / 8];

        for (int i = 0; i < byteArray.length; i++) {
            for (int j = 0; j < 8; j++) {
                byteArray[i] |= (boolArray[i * 8 + j] ? 1 : 0) << j;
            }
        }
        return byteArray;
    }

    public static boolean[][] divideBooleanArray8(boolean[] boolArray) {
        int size = boolArray.length;
        int numArrays = (size + 7) / 8;
        boolean[][] result = new boolean[numArrays][8];

        for (int i = 0; i < numArrays; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i * 8 + j) < size) {
                    result[i][j] = boolArray[i * 8 + j];
                } else {
                    result[i][j] = false;
                }
            }
        }
        return result;
    }

    public static boolean[] mergeBooleanArray8(boolean[][] boolArrays) {
        int numRows = boolArrays.length;
        boolean[] result = new boolean[numRows * 8];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i * 8 + j) < result.length) {
                    result[i * 8 + j] = boolArrays[i][j];
                }
            }
        }
        return result;
    }
}

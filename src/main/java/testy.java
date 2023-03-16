
import library.ReedSolomon;


import java.util.Arrays;

public class testy {
    public static void main(String[] args) {
        ReedSolomon reedSolomon = new ReedSolomon(4, 2);
        byte [][] shards = {
                new byte []{1,0,1,0},
                new byte []{1,0,0,1},
                new byte []{0,0,1,0},
                new byte []{1,1,1,1},
                new byte []{0,0,0,0},
                new byte []{0,0,0,0}
        };
        reedSolomon.encodeParity(shards, 0, 4);

        int numRows = shards.length;
        int numCols = shards[0].length;

        // Zakoduj tablicę bajtów na ciąg zer i jedynek
        String encodedData = BinaryEncoding.encode(shards);
        System.out.println("Encoded data: " + encodedData);

        // Odkoduj ciąg zer i jedynek na tablicę bajtów
        byte[][] decodedData = BinaryEncoding.decode(encodedData, numRows, numCols);
        System.out.println("Decoded data: " + Arrays.deepToString(decodedData));

        System.out.println(Arrays.deepToString(shards));
        System.out.println(reedSolomon.isParityCorrect(shards, 0, 4));
        boolean[] present = {true, true, true, true, true, true};
        shards[0] = new byte[]{0, 0, 0, 0};
        System.out.println(Arrays.deepToString(shards));
        reedSolomon.decodeMissing(shards,present,0,4);
        System.out.println(Arrays.deepToString(shards));
    }
}
class BinaryEncoding {
    // Funkcja kodująca tablicę bajtów na ciąg zer i jedynek
    public static String encode(byte[][] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                // Konwertuj każdy bajt na jego reprezentację binarną i dodaj do wynikowego ciągu
                sb.append(String.format("%8s", Integer.toBinaryString(data[i][j] & 0xFF)).replace(' ', '0'));
            }
        }
        return sb.toString();
    }

    // Funkcja dekodująca ciąg zer i jedynek na tablicę bajtów
    public static byte[][] decode(String binaryString, int numRows, int numCols) {
        byte[][] result = new byte[numRows][numCols];
        for (int i = 0; i < binaryString.length(); i += 8) {
            String binaryByte = binaryString.substring(i, i + 8);
            // Konwertuj każde 8-bitowe ciąg zer i jedynek na odpowiadający mu bajt
            result[i / 8 / numCols][i / 8 % numCols] = (byte) Integer.parseInt(binaryByte, 2);
        }
        return result;
    }
}
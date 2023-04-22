package syf;

import BCHcode.BCH;

public class testy {
    public static void main(String[] args) {
        BCH bch = new BCH(15, 7);
        String m;
        String c;
        int length_m = bch.getM_length();
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < length_m; j++)
            sb.append((int) (Math.random() + 0.5));
        m = sb.toString();
        sb = new StringBuilder();
        System.out.println("\nWiadomość = " + m);
        c = bch.encoding(m);
        System.out.println("Słowo kodowe = " + c);
        char[] chars = c.toCharArray();
        for (int j = 0; j < c.length(); j++)
            sb.append((Math.random() > 0.75 ? (chars[j] == '1' ? '0' : '1') : chars[j]));
        String newC = sb.toString();
        System.out.println("Odebrane słowo kodowe = " + newC);
        System.out.println("Zdekodowana wiadomość = " + bch.decodingBM(newC));
    }
}

//class BinaryEncoding {
//    // Funkcja kodująca tablicę bajtów na ciąg zer i jedynek
//    public static String encode(byte[][] data) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < data.length; i++) {
//            for (int j = 0; j < data[i].length; j++) {
//                // Konwertuj każdy bajt na jego reprezentację binarną i dodaj do wynikowego ciągu
//                sb.append(String.format("%8s", Integer.toBinaryString(data[i][j] & 0xFF)).replace(' ', '0'));
//            }
//        }
//        return sb.toString();
//    }
//
//    // Funkcja dekodująca ciąg zer i jedynek na tablicę bajtów
//    public static byte[][] decode(String binaryString, int numRows, int numCols) {
//        byte[][] result = new byte[numRows][numCols];
//        for (int i = 0; i < binaryString.length(); i += 8) {
//            String binaryByte = binaryString.substring(i, i + 8);
//            // Konwertuj każde 8-bitowe ciąg zer i jedynek na odpowiadający mu bajt
//            result[i / 8 / numCols][i / 8 % numCols] = (byte) Integer.parseInt(binaryByte, 2);
//        }
//        return result;
//    }
//}
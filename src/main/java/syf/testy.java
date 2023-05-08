//package syf;
//
//import BCHcode.BCH;
//import codes.HammingCode;
//
//import java.util.Arrays;
//
//public class testy {
//    public static void main(String[] args) {
//        BCH bch = new BCH(15, 7);
//        String m;
//        String c;
//        int length_m = bch.getM_length();
//        StringBuilder sb = new StringBuilder();
//        for (int j = 0; j < length_m; j++)
//            sb.append((int) (Math.random() + 0.5));
//        m = sb.toString();
//        sb = new StringBuilder();
//        System.out.println("\nWiadomość = " + m);
//        c = bch.encoding(m);
//        System.out.println("Słowo kodowe = " + c);
//        char[] chars = c.toCharArray();
//        for (int j = 0; j < c.length(); j++)
//            sb.append((Math.random() > 0.95 ? (chars[j] == '1' ? '0' : '1') : chars[j]));
//        String newC = sb.toString();
//        System.out.println("Odebrane słowo kodowe = " + newC);
//        System.out.println("Zdekodowana wiadomość = " + bch.decodingBM(newC));
//    }
//}
package syf;

import BCHcode.BCH;
import codes.HammingCode;

import java.util.Arrays;

public class testy {
    public static void main(String[] args) {
//        BCH bch = new BCH(15, 7);
//        StringBuilder sb = new StringBuilder();
//
//        String m = "11001";
//        System.out.println("\nWiadomość = " + m);
//        String c = bch.encoding(m);
//        System.out.println("Słowo kodowe = " + c);
//        System.out.println("Odebrane słowo kodowe = " + c);
//        char[] chars = c.toCharArray();
//        for (int j = 0; j < c.length(); j++)
//            sb.append((Math.random() > 0.60 ? (chars[j] == '1' ? '0' : '1') : chars[j]));
//        String newC = sb.toString();
//        System.out.println("Zdekodowana wiadomość = " + bch.decodingBM(newC));
        HammingCode hammingCode = new HammingCode(5);
        boolean[] message = {false, false, true, false, false};
        boolean[] encode = hammingCode.encode(message);
        System.out.println(Arrays.toString(encode));
        boolean[] decode = hammingCode.decode(encode);
        System.out.println(Arrays.toString(decode));
    }
}
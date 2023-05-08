package codes;

import BCHcode.BCH;

public class BchCode implements ErrorCorrectionCode{
    BCH bch = new BCH(15, 7);
    @Override
    public boolean[] encode(boolean[] message) {
        String m = booleanArrayToString(message);
        return stringToBooleanArray(bch.encoding(m));
    }

    @Override
    public boolean[] decode(boolean[] receivedMessage) {
        String c = booleanArrayToString(receivedMessage);
        return stringToBooleanArray(bch.decodingBM(c));
    }

    public static boolean[] stringToBooleanArray(String input) {
        boolean[] result = new boolean[input.length()];
        for (int i = 0; i < input.length(); i++) {
            result[i] = input.charAt(i) == '1';
        }
        return result;
    }
    public static String booleanArrayToString(boolean[] input) {
        StringBuilder sb = new StringBuilder();
        for (boolean b : input) {
            sb.append(b ? '1' : '0');
        }
        return sb.toString();
    }
}

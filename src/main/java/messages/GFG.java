package messages;

public class GFG {

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

public static boolean[][] divideBooleanArray(boolean[] boolArray) {
    int size = boolArray.length;
    int numArrays = (size + 127) / 128;
    boolean[][] result = new boolean[numArrays][128];

    for (int i = 0; i < numArrays; i++) {
        for (int j = 0; j < 128; j++) {
            if ((i * 128 + j) < size) {
                result[i][j] = boolArray[i * 128 + j];
            } else {
                result[i][j] = false;
            }
        }
    }
    return result;
}

    public static boolean[] mergeBooleanArray(boolean[][] boolArrays) {
        int numRows = boolArrays.length;
        boolean[] result = new boolean[numRows * 128];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < 128; j++) {
                if ((i * 128 + j) < result.length) {
                    result[i * 128 + j] = boolArrays[i][j];
                }
            }
        }
        return result;
    }
}

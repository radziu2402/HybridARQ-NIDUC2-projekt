package codes;


public class HammingCode implements ErrorDetectionCode {

    private final int messageLength;
    private final int parityBits;
    private final int codewordLength;

    public HammingCode(int messageLength) {
        this.messageLength = messageLength;
        this.parityBits = calculateParityBits(messageLength);
        this.codewordLength = messageLength + parityBits;
    }

    private int calculateParityBits(int messageLength) {
        int r = 0;
        while (Math.pow(2, r) < messageLength + r + 1) {
            r++;
        }
        return r;
    }

    @Override
    public boolean[] encode(boolean[] message) {
        boolean[] encodedMessage = new boolean[codewordLength];
        int j = 0;
        for (int i = 0; i < codewordLength; i++) {
            if (isPowerOfTwo(i + 1)) {
                encodedMessage[i] = false;
            } else {
                encodedMessage[i] = message[j];
                j++;
            }
        }
        for (int i = 0; i < parityBits; i++) {
            int parityIndex = (int) Math.pow(2, i) - 1;
            boolean parityValue = calculateParityValue(encodedMessage, parityIndex);
            encodedMessage[parityIndex] = parityValue;
        }
        return encodedMessage;
    }

    private boolean isPowerOfTwo(int n) {
        // Sprawdza, czy liczba jest potęgą dwójki.
        // Zwraca true, jeśli tak, lub false w przeciwnym przypadku.
        return (n & (n - 1)) == 0;
    }

    private boolean calculateParityValue(boolean[] codeword, int parityIndex) {
        // Oblicza wartość bitu parzystości dla danej pozycji w ciągu kodowym.
        // Zwraca true, jeśli liczba jedynek na pozycjach w ciągu kodowym
        // odpowiadających wartości bitów parzystości jest nieparzysta,
        // lub false w przeciwnym przypadku.
        boolean parityValue = codeword[parityIndex];
        for (int i = parityIndex + 1; i < codewordLength; i++) {
            if (!isPowerOfTwo(i + 1) && ((i + 1) & (parityIndex + 1)) != 0 && codeword[i]) {
                parityValue = !parityValue;
            }
        }
        return parityValue;
    }

    @Override
    public boolean[] decode(boolean[] receivedMessage) {
        boolean[] decodedMessage = new boolean[messageLength];
        int j = 0;
        for (int i = 0; i < codewordLength; i++) {
            if (!isPowerOfTwo(i + 1)) {
                decodedMessage[j] = receivedMessage[i];
                j++;
            }
        }
        for (int i = 0; i < parityBits; i++) {
            int parityIndex = (int) Math.pow(2, i) - 1;
            boolean parityValue = calculateParityValue(receivedMessage, parityIndex);
            if (receivedMessage[parityIndex] != parityValue) {
                return null;
            }
        }
        return decodedMessage;
    }
}
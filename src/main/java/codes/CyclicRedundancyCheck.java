package codes;

public class CyclicRedundancyCheck implements ErrorCorrectionCode {
    private final int[] generatorPolynomial;

    public CyclicRedundancyCheck(int[] generatorPolynomial) {
        this.generatorPolynomial = generatorPolynomial;
    }
    public static boolean[] intArrayToBoolArray(int[] intArray) {
        boolean[] boolArray = new boolean[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            boolArray[i] = (intArray[i] == 1);
        }
        return boolArray;
    }
    public boolean[] encode(boolean[] message) {
        // Copy the message into a new array and append zeros
        boolean[] encodedMessage = new boolean[message.length + generatorPolynomial.length - 1];
        System.arraycopy(message, 0, encodedMessage, 0, message.length);

        // Perform polynomial division to calculate the remainder
        for (int i = 0; i < message.length; i++) {
            if (encodedMessage[i]) {
                for (int j = 0; j < generatorPolynomial.length; j++) {
                    encodedMessage[i + j] ^= generatorPolynomial[j] != 0;
                }
            }
        }
        System.arraycopy(message, 0, encodedMessage, 0, message.length);
        return encodedMessage;
    }

    public boolean[] decode(boolean[] receivedMessage) {
        // Copy the received message into a new array
        boolean[] decodedMessage = new boolean[receivedMessage.length];
        System.arraycopy(receivedMessage, 0, decodedMessage, 0, receivedMessage.length);

        // Perform polynomial division to check for errors
        for (int i = 0; i < receivedMessage.length - generatorPolynomial.length + 1; i++) {
            if (decodedMessage[i]) {
                for (int j = 0; j < generatorPolynomial.length; j++) {
                    decodedMessage[i + j] ^= generatorPolynomial[j] != 0;
                }
            }
        }

//        // If the remainder is not zero, then there is an error
//        for (int i = receivedMessage.length - generatorPolynomial.length + 1; i < receivedMessage.length; i++) {
//            if (decodedMessage[i]) {
//                return null; // Error detected
//            }
//        }

        // Remove the zeros appended during encoding
        boolean[] originalMessage = new boolean[receivedMessage.length - generatorPolynomial.length + 1];
        System.arraycopy(receivedMessage, 0, originalMessage, 0, originalMessage.length);
        return originalMessage;
    }
}
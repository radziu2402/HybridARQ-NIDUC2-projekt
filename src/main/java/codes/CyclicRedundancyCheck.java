package codes;

public class CyclicRedundancyCheck implements ErrorCorrectionCode {
    private final int[] generatorPolynomial;

    public CyclicRedundancyCheck(int[] generatorPolynomial) {
        this.generatorPolynomial = generatorPolynomial;
    }

    @Override
    public boolean[] encode(boolean[] message) {
        int n = message.length;
        int r = generatorPolynomial.length - 1;
        boolean[] encodedMessage = new boolean[n + r];

        System.arraycopy(message, 0, encodedMessage, 0, n);

        while (n < encodedMessage.length) {
            if (encodedMessage[n]) {
                for (int i = 0; i < generatorPolynomial.length; i++) {
                    encodedMessage[n + i] ^= generatorPolynomial[i] == 1;
                }
            }
            n++;
        }

        return encodedMessage;
    }

    @Override
    public boolean[] decode(boolean[] receivedMessage) {
        int n = receivedMessage.length - generatorPolynomial.length + 1;
        boolean[] decodedMessage = new boolean[n];

        System.arraycopy(receivedMessage, 0, decodedMessage, 0, n);

        while (n < receivedMessage.length) {
            if (receivedMessage[n]) {
                for (int i = 0; i < generatorPolynomial.length; i++) {
                    receivedMessage[n + i] ^= generatorPolynomial[i] == 1;
                }
            }
            n++;
        }

        for (int i = 0; i < n; i++) {
            if (receivedMessage[receivedMessage.length - i - 1]) {
                return null; // błąd niekorygowalny
            }
        }

        return decodedMessage;
    }
}
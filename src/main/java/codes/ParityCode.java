package codes;

public class ParityCode implements ErrorDetectionCode {
    @Override
    public boolean[] encode(boolean[] message) {
        boolean[] encodedMessage = new boolean[message.length + 1];
        int onesCount = 0;
        for (boolean bit : message) {
            if (bit) {
                onesCount++;
            }
        }
        encodedMessage[encodedMessage.length - 1] = onesCount % 2 == 1; // parity bit at end
        System.arraycopy(message, 0, encodedMessage, 0, message.length);
        return encodedMessage;
    }

    @Override
    public boolean[] decode(boolean[] receivedMessage) {
        int onesCount = 0;
        for (int i = 0; i < receivedMessage.length; i++) {
            if (receivedMessage[i]) {
                onesCount++;
            }
        }
        boolean parityBit = onesCount % 2 == 1;
        if (receivedMessage[receivedMessage.length - 1] != parityBit) {
            return null; // error detected, return null
        } else {
            boolean[] decodedMessage = new boolean[receivedMessage.length - 1];
            System.arraycopy(receivedMessage, 0, decodedMessage, 0, decodedMessage.length);
            return decodedMessage;
        }
    }
}

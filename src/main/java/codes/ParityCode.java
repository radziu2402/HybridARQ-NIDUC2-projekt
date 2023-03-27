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
        encodedMessage[0] = onesCount % 2 == 1; // bit parzystości
        System.arraycopy(message, 0, encodedMessage, 1, message.length);
        return encodedMessage;
    }

    @Override
    public boolean[] decode(boolean[] receivedMessage) {
        boolean[] decodedMessage = new boolean[receivedMessage.length - 1];
        System.arraycopy(receivedMessage, 1, decodedMessage, 0, decodedMessage.length);
        return decodedMessage;
    }
}

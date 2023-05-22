package channels;


public class BinarySymmetricChannel implements Channel {
    private final double errorProbability;

    public BinarySymmetricChannel(double errorProbability) {
        this.errorProbability = errorProbability;
    }

    @Override
    public boolean[] transmit(boolean[] message) {
        boolean[] receivedMessage = new boolean[message.length];

        for (int i = 0; i < message.length; i++) {
            receivedMessage[i] = transmitBit(message[i]);
        }

        return receivedMessage;
    }

    private boolean transmitBit(boolean bit) {
        if (Math.random() <= errorProbability) {
            return !bit;
        } else {
            return bit;
        }
    }
}

package messages;

public class ErrorDetector {
    public static boolean detectErrors(boolean[] originalMessage, boolean[] receivedMessage) {
        if (originalMessage.length != receivedMessage.length) {
            throw new IllegalArgumentException("Original and received messages must have the same length.");
        }

        for (int i = 0; i < originalMessage.length; i++) {
            if (originalMessage[i] != receivedMessage[i]) {
                return true;
            }
        }

        return false;
    }
}

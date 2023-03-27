package codes;

public interface ErrorDetectionCode {
    boolean[] encode(boolean[] message);

    boolean[] decode(boolean[] receivedMessage);

}

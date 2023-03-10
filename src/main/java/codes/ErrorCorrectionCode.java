package codes;

public interface ErrorCorrectionCode {

        boolean[] encode(boolean[] message);

        boolean[] decode(boolean[] receivedMessage);

}

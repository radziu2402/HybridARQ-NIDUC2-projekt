
import channels.Channel;
import codes.ErrorCorrectionCode;

public class HybridAutomaticRepeatRequest {
    private final Channel channel;
    private final ErrorCorrectionCode errorCorrectionCode;
    private final int maxRetransmissions;

    public HybridAutomaticRepeatRequest(Channel channel, ErrorCorrectionCode errorCorrectionCode, int maxRetransmissions) {
        this.channel = channel;
        this.errorCorrectionCode = errorCorrectionCode;
        this.maxRetransmissions = maxRetransmissions;
    }

    public boolean[] transmit(boolean[] message) {
        int retransmissions = 0;
        boolean[] encodedMessage = errorCorrectionCode.encode(message);

        while (retransmissions < maxRetransmissions) {
            boolean[] receivedMessage = channel.transmit(encodedMessage);
            boolean[] decodedMessage = errorCorrectionCode.decode(receivedMessage);

            if (decodedMessage != null) {
                return decodedMessage;
            }

            retransmissions++;
        }

        return null; // błąd niekorygowalny
    }
}

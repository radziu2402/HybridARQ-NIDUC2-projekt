package channels;

public class GilbertElliottChannel implements Channel {
    private final double goodToBadProbability;
    private final double badToGoodProbability;
    private final double goodChannelErrorProbability;
    private final double badChannelErrorProbability;

    public GilbertElliottChannel(double goodToBadProbability, double badToGoodProbability, double goodChannelErrorProbability, double badChannelErrorProbability) {
        this.goodToBadProbability = goodToBadProbability;
        this.badToGoodProbability = badToGoodProbability;
        this.goodChannelErrorProbability = goodChannelErrorProbability;
        this.badChannelErrorProbability = badChannelErrorProbability;
    }

    @Override
    public boolean[] transmit(boolean[] message) {
        boolean[] receivedMessage = new boolean[message.length];
        boolean inGoodState = true;

        for (int i = 0; i < message.length; i++) {
            if (inGoodState) {
                if (Math.random() < goodChannelErrorProbability) {
                    receivedMessage[i] = !message[i];
                } else {
                    receivedMessage[i] = message[i];
                }
                if (Math.random() < goodToBadProbability) {
                    inGoodState = false;
                }
            } else {
                if (Math.random() < badChannelErrorProbability) {
                    receivedMessage[i] = !message[i];
                } else {
                    receivedMessage[i] = message[i];
                }
                if (Math.random() < badToGoodProbability) {
                    inGoodState = true;
                }
            }
        }
        return receivedMessage;
    }
}

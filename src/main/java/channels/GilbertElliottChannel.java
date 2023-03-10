package channels;

public class GilbertElliottChannel implements Channel {
    private final double errorProbability;
    private final double switchProbability;
    private final double burstProbability;

    public GilbertElliottChannel(double errorProbability, double switchProbability, double burstProbability) {
        this.errorProbability = errorProbability;
        this.switchProbability = switchProbability;
        this.burstProbability = burstProbability;
    }

    @Override
    public boolean[] transmit(boolean[] message) {
        boolean[] receivedMessage = new boolean[message.length];
        boolean inGoodState = true;
        int burstLength = 0;

        for (int i = 0; i < message.length; i++) {
            if (inGoodState) {
                if (Math.random() < errorProbability) {
                    receivedMessage[i] = !message[i];
                } else {
                    receivedMessage[i] = message[i];
                }
                if (Math.random() < switchProbability) {
                    inGoodState = false;
                    burstLength = 1;
                }
            } else {
                if (Math.random() < burstProbability) {
                    receivedMessage[i] = !message[i];
                    burstLength++;
                } else {
                    receivedMessage[i] = message[i];
                    if (++burstLength >= 3) {
                        inGoodState = true;
                    }
                }
            }
        }

        return receivedMessage;
    }
}


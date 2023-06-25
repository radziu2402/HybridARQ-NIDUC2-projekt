package channels;

public class GilbertElliottChannel implements Channel {
    private final double goodToBadProbability;
    private final double badToGoodProbability;
    private final double goodChannelErrorProbability;
    private final double badChannelErrorProbability;
    private boolean inGoodState;
    public static int goodTransmissions;
    public static int badTransmissions;

    public GilbertElliottChannel(double goodToBadProbability, double badToGoodProbability, double goodChannelErrorProbability, double badChannelErrorProbability) {
        this.goodToBadProbability = goodToBadProbability;
        this.badToGoodProbability = badToGoodProbability;
        this.goodChannelErrorProbability = goodChannelErrorProbability;
        this.badChannelErrorProbability = badChannelErrorProbability;
        this.inGoodState = true; // początkowy stan kanału
        goodTransmissions = 0;
        badTransmissions = 0;
    }

    @Override
    public boolean[] transmit(boolean[] message) {
        boolean[] receivedMessage = new boolean[message.length];

        for (int i = 0; i < message.length; i++) {
            if (inGoodState) {
                if (Math.random() < goodChannelErrorProbability) {
                    receivedMessage[i] = !message[i];
                } else {
                    receivedMessage[i] = message[i];
                }
                if (Math.random() < goodToBadProbability) {
                    inGoodState = false;
                    badTransmissions++;
                    //System.out.println("Kanał zmienił stan na zły");
                } else {
                    goodTransmissions++;
                }
            } else {
                if (Math.random() < badChannelErrorProbability) {
                    receivedMessage[i] = !message[i];
                } else {
                    receivedMessage[i] = message[i];
                }
                if (Math.random() < badToGoodProbability) {
                    inGoodState = true;
                    goodTransmissions++;
                    //System.out.println("Kanał zmienił stan na dobry");
                } else {
                    badTransmissions++;
                }
            }
        }
        return receivedMessage;
    }

    public static double getGoodStatePercentage() {
        int totalTransmissions = goodTransmissions + badTransmissions;
        if (totalTransmissions == 0) {
            return 0.0;
        }
        return (double) goodTransmissions / totalTransmissions * 100;
    }

    public static double getBadStatePercentage() {
        int totalTransmissions = goodTransmissions + badTransmissions;
        if (totalTransmissions == 0) {
            return 0.0;
        }
        return (double) badTransmissions / totalTransmissions * 100;
    }
}

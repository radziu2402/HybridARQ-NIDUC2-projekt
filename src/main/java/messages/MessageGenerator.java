package messages;

import java.util.Random;

public class MessageGenerator {

    public static boolean[] generateRandomMessage(int length) {
        Random random = new Random();
        boolean[] message = new boolean[length];

        for (int i = 0; i < length; i++) {
            message[i] = random.nextBoolean();
        }

        return message;
    }

}
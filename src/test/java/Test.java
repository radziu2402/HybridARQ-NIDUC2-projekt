import codes.CyclicRedundancyCheck;
import messages.MessageGenerator;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

public class Test {
    @org.junit.jupiter.api.Test
    public void checkEncodeAndDecodeCRC8() {
        int[] crc8Poly = {1, 0, 0, 1, 1, 0, 0, 0, 0};
        int messageLength = 4;
        CyclicRedundancyCheck cyclicRedundancyCheck = new CyclicRedundancyCheck(crc8Poly);
        boolean[] message = MessageGenerator.generateRandomMessage(messageLength);
        System.out.println(Arrays.toString(message));
        boolean[] encodedMessage = cyclicRedundancyCheck.encode(message);
        System.out.println(Arrays.toString(encodedMessage));
        boolean[] decodedMessage = cyclicRedundancyCheck.decode(encodedMessage);
        System.out.println(Arrays.toString(decodedMessage));
        Assertions.assertArrayEquals(message,decodedMessage);
    }
    @org.junit.jupiter.api.Test
    public void checkEncodeAndDecodeHamming() {
        int messageLength = 4;
        boolean[] message = MessageGenerator.generateRandomMessage(messageLength);
        System.out.println(Arrays.toString(message));
        HammingCode hammingCode = new HammingCode(8);
        boolean[] encodedMessage = hammingCode.encode(message);
        System.out.println(Arrays.toString(encodedMessage));
        boolean[] decodedMessage = hammingCode.decode(encodedMessage);
        System.out.println(Arrays.toString(decodedMessage));
        Assertions.assertArrayEquals(message,decodedMessage);
    }
}

import channels.BinarySymmetricChannel;
import channels.Channel;
import channels.GilbertElliottChannel;
import codes.*;
import messages.MessageGenerator;
import messages.MessagePrinter;


import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Wybierz kanał:");
        System.out.println("1. Binary Symmetric Channel");
        System.out.println("2. Gilbert-Elliott Channel");
        int channelChoice = scanner.nextInt();

        System.out.println("Podaj długość wiadomości:");
        int messageLength = scanner.nextInt();

        // Wybór kodu detekcyjnego
        System.out.println("Wybierz kod detekcyjny: ");
        System.out.println("1. Bit parzystości");
        System.out.println("2. CRC8 Code");
        System.out.println("3. CRC16 Code");
        System.out.println("4. CRC32 Code");
        int errorDetectionCodeChoice = scanner.nextInt();

        ErrorDetectionCode errorDetectionCode;
        if (errorDetectionCodeChoice == 1) {
            errorDetectionCode = new ParityCode();
        } else if (errorDetectionCodeChoice == 2) {
            int[] crc8Poly = {1, 0, 0, 1, 1, 0, 0, 0, 0}; // CRC-8 polynomial: x^8 + x^2 + x + 1
            errorDetectionCode = new CyclicRedundancyCheck(crc8Poly);
        } else if (errorDetectionCodeChoice == 3) {
            int[] crc16Poly = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1}; // CRC-16 polynomial: x^16 + x^15 + x^2 + 1
            errorDetectionCode = new CyclicRedundancyCheck(crc16Poly);
        } else if (errorDetectionCodeChoice == 4) {
            int[] crc32Poly = {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}; // CRC-32 polynomial: 0x04C11DB7
            errorDetectionCode = new CyclicRedundancyCheck(crc32Poly);
        } else {
            System.out.println("Niepoprawny wybór kodu detekcyjnego.");
            return;
        }

        // Wybór kodu korekcyjnego
        System.out.println("Wybierz kod korekcyjny: ");
        System.out.println("1. Kod Hamminga");
        int errorCorrectionCodeChoice = scanner.nextInt();

        ErrorCorrectionCode errorCorrectionCode;
        if (errorCorrectionCodeChoice == 1) {
            errorCorrectionCode = new HammingCode(12);
        } else {
            System.out.println("Niepoprawny wybór kodu korekcyjnego.");
            return;
        }

        Channel channel;
        if (channelChoice == 1) {
            System.out.println("Podaj prawdopodobieństwo przekłamania bitu w kanale transmisyjnym (w przedziale 0-1): ");
            double channelParam = scanner.nextDouble();
            channel = new BinarySymmetricChannel(channelParam);
        } else {
            System.out.println("Podaj wartość pierwszego parametru dla kanału:");
            double channelParam = scanner.nextDouble();
            System.out.println("Podaj wartość drugiego parametru dla kanału:");
            double secondChannelParam = scanner.nextDouble();
            System.out.println("Podaj wartość trzeciego parametru dla kanału:");
            double thirdChannelParam = scanner.nextDouble();
            channel = new GilbertElliottChannel(channelParam, secondChannelParam, thirdChannelParam);
        }
        System.out.println("Podaj maksymalna liczbę retransmisji");
        int maxRetransmissions = scanner.nextInt();
        int retransmissionCount = 0;
        boolean[] detectionDecodedMessage = null;
        boolean[] originalMessage = MessageGenerator.generateRandomMessage(messageLength);
        while(detectionDecodedMessage==null && retransmissionCount<=maxRetransmissions) {
            if(retransmissionCount!=0){
                System.out.println("To już " + retransmissionCount + " retransmisja");
            }
            System.out.println("Wiadomość oryginalna:");
            MessagePrinter.printMessage(originalMessage);

            boolean[] detectionEncodedMessage = errorDetectionCode.encode(originalMessage);
            System.out.println("Wiadomość zakodowana kodem detekcyjnym:");
            MessagePrinter.printMessage(detectionEncodedMessage);

            boolean[] correctionEncodedMessage = errorCorrectionCode.encode(detectionEncodedMessage);
            System.out.println("Wiadomość zakodowana kodem korekcyjnym:");
            MessagePrinter.printMessage(correctionEncodedMessage);


            boolean[] receivedMessage = channel.transmit(correctionEncodedMessage);
            System.out.println("Wiadomość odebrana:");
            MessagePrinter.printMessage(receivedMessage);

            boolean[] correctionDecodedMessage = errorCorrectionCode.decode(receivedMessage);
            detectionDecodedMessage = errorDetectionCode.decode(correctionDecodedMessage);
            if (detectionDecodedMessage != null) {
                System.out.println("Wiadomość zdekodowana");
                MessagePrinter.printMessage(detectionDecodedMessage);
            }
            else{
                System.out.println("Potrzebna retransmisja");
                retransmissionCount++;
            }
        }
    }
}

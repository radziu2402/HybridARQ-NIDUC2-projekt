import channels.BinarySymmetricChannel;
import channels.Channel;
import channels.GilbertElliottChannel;
import codes.CyclicRedundancyCheck;
import codes.ErrorCorrectionCode;
import codes.HammingCode;
import codes.ParityCode;
import messages.ErrorDetector;
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

        System.out.println("Wybierz kod:");
        System.out.println("1. Parity Code");
        System.out.println("2. CRC16 Code");
        System.out.println("3. Hamming Code");
        int codeChoice = scanner.nextInt();

        System.out.println("Podaj długość wiadomości:");
        int messageLength = scanner.nextInt();

        System.out.println("Podaj liczbę iteracji HARQ:");
        int harqIterations = scanner.nextInt();

        System.out.println("Podaj wartość parametru dla kanału:");
        double channelParam = scanner.nextDouble();

        Channel channel;
        if (channelChoice == 1) {
            channel = (Channel) new BinarySymmetricChannel(channelParam);
        } else {
            System.out.println("Podaj wartość drugiego parametru dla kanału:");
            double secondChannelParam = scanner.nextDouble();
            System.out.println("Podaj wartość trzeciego parametru dla kanału:");
            double thirdChannelParam = scanner.nextDouble();
            channel = (Channel) new GilbertElliottChannel(channelParam, secondChannelParam, thirdChannelParam);
        }

        ErrorCorrectionCode errorCorrectionCode;
        if (codeChoice == 1) {
            errorCorrectionCode = new ParityCode();
        } else if (codeChoice == 2) {
            errorCorrectionCode = new CyclicRedundancyCheck(new int[]{0, 2, 15});
        } else {
            errorCorrectionCode = new HammingCode(messageLength);
        }

        for (int i = 0; i < harqIterations; i++) {
            System.out.println("Iteracja " + (i + 1) + ":");

            boolean[] originalMessage = MessageGenerator.generateRandomMessage(messageLength);
            System.out.println("Wiadomość oryginalna:");
            MessagePrinter.printMessage(originalMessage);

            boolean[] encodedMessage = errorCorrectionCode.encode(originalMessage);
            System.out.println("Wiadomość zakodowana:");
            MessagePrinter.printMessage(encodedMessage);

            boolean[] receivedMessage = channel.transmit(encodedMessage);
            System.out.println("Wiadomość odebrana:");
            MessagePrinter.printMessage(receivedMessage);

            boolean[] decodedMessage = errorCorrectionCode.decode(receivedMessage);
            System.out.println("Wiadomość zdekodowana:");
            MessagePrinter.printMessage(decodedMessage);

            boolean hasError = ErrorDetector.detectErrors(originalMessage, decodedMessage);
            if (hasError) {
                System.out.println("Wystąpił błąd w transmisji.");
            } else {
                System.out.println("Transmisja przebiegła pomyślnie.");
                break;
            }
        }
    }
}

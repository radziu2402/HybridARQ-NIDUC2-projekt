import channels.BinarySymmetricChannel;
import channels.Channel;
import channels.GilbertElliottChannel;
import codes.*;
import com.casualcoding.reedsolomon.EncoderDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import messages.GFG;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) throws IOException, EncoderDecoder.DataTooLargeException, ReedSolomonException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Wybierz kanał:");
        System.out.println("1. Binary Symmetric Channel");
        System.out.println("2. Gilbert-Elliott Channel");
        int channelChoice = scanner.nextInt();

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
            int[] crc8Poly = {1, 1, 1, 0, 0, 1, 1, 1, 1}; // CRC-8
            errorDetectionCode = new CyclicRedundancyCheck(crc8Poly);
        } else if (errorDetectionCodeChoice == 3) {
            int[] crc16Poly = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1}; // CRC-16
            errorDetectionCode = new CyclicRedundancyCheck(crc16Poly);
        } else if (errorDetectionCodeChoice == 4) {
            int[] crc32Poly = {1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1}; // CRC-32
            errorDetectionCode = new CyclicRedundancyCheck(crc32Poly);
        } else {
            System.out.println("Niepoprawny wybór kodu detekcyjnego.");
            return;
        }

        // Wybór kodu korekcyjnego
        System.out.println("Wybierz kod korekcyjny: ");
        System.out.println("1. Kod RS");

        int errorCorrectionCodeChoice = scanner.nextInt();

        EncoderDecoder RSencoderDecoder;
        if (errorCorrectionCodeChoice == 1) {
            RSencoderDecoder = new EncoderDecoder();
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
            System.out.println("Podaj wartość pierwszego parametru dla kanału(goodToBadProbability):");
            double channelParam = scanner.nextDouble();
            System.out.println("Podaj wartość drugiego parametru dla kanału(badToGoodProbability):");
            double secondChannelParam = scanner.nextDouble();
            System.out.println("Podaj wartość trzeciego parametru dla kanału(goodChannelErrorProbability):");
            double thirdChannelParam = scanner.nextDouble();
            System.out.println("Podaj wartość czwartego parametru dla kanału(badChannelErrorProbability):");
            double fourthChannelParam = scanner.nextDouble();
            channel = new GilbertElliottChannel(channelParam, secondChannelParam, thirdChannelParam, fourthChannelParam);
        }

        BufferedImage image = ImageIO.read(new File("src\\main\\resources\\sample.bmp"));
        // create the object of ByteArrayOutputStream class
        ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
        // write the image into the object of ByteArrayOutputStream class
        ImageIO.write(image, "BMP", outStreamObj);

        // create the byte array from image
        byte[] byteArray = outStreamObj.toByteArray();
        boolean[] message = GFG.byteArrayToBooleanArray(byteArray);
        boolean[][] originalMessage = GFG.divideBooleanArray(message);
        ArrayList<boolean[]> decodedMessages = new ArrayList<>();
        int k = 1;
        int[] successCounters = new int[originalMessage.length + 2];
        for (boolean[] booleans : originalMessage) {
            System.out.println("Pakiet nr " + k);
//            System.out.println("Wiadomość oryginalna:");
//            MessagePrinter.printMessage(booleans);
            k++;
            boolean[] detectionEncodedMessage = errorDetectionCode.encode(booleans);
//            System.out.println("Wiadomość zakodowana kodem detekcyjnym:");
//            MessagePrinter.printMessage(detectionEncodedMessage);

            boolean[] receivedMessage = channel.transmit(detectionEncodedMessage);
//            System.out.println("Wiadomość odebrana:");
//            MessagePrinter.printMessage(receivedMessage);

            boolean[] detectionDecodedMessage = errorDetectionCode.decode(receivedMessage);
            if (detectionDecodedMessage != null) {
//                System.out.println("Wiadomość zdekodowana poprawnie");
//                MessagePrinter.printMessage(detectionDecodedMessage);
                decodedMessages.add(detectionDecodedMessage);
                successCounters[k] = 1;
            } else {
//                System.out.println("Wystąpił błąd w transmisji, dosyłam część korekcyjna");
                byte[] bytes = GFG.booleanArrayToByteArray(booleans);
                byte[] encode = RSencoderDecoder.encodeData(bytes, 6);
                boolean[] booleans1 = GFG.byteArrayToBooleanArray(encode);
//                System.out.println("Wiadomość zakodowana korekcyjnym:");
//                MessagePrinter.printMessage(booleans1);
                boolean[] receivedMessage1 = channel.transmit(booleans1);
//                System.out.println("Wiadomość odebrana:");
//                MessagePrinter.printMessage(receivedMessage1);
                byte[] bytes1 = GFG.booleanArrayToByteArray(receivedMessage1);
                byte[] correctionDecodedMessage = RSencoderDecoder.decodeData(bytes1, 6);
                boolean[] receivedMessage2;
                successCounters[k] = 2;
                int i = 2;
                while (correctionDecodedMessage == null) {
                    i++;
                    successCounters[k] = i;
//                    System.out.println("Za duzo bledow w czesci korekcyjnej wiec wysylamy ja znowu");
                    receivedMessage2 = channel.transmit(booleans1);
//                    System.out.println("Wiadomość odebrana:");
//                    MessagePrinter.printMessage(receivedMessage2);
                    bytes1 = GFG.booleanArrayToByteArray(receivedMessage2);
                    correctionDecodedMessage = RSencoderDecoder.decodeData(bytes1, 6);
                }
                boolean[] booleans2 = GFG.byteArrayToBooleanArray(correctionDecodedMessage);
//                System.out.println("Wiadomość zdekodowana poprawnie");
//                MessagePrinter.printMessage(booleans2);
                decodedMessages.add(booleans2);
            }
//            System.out.println(" ");
        }

        boolean[][] results = new boolean[decodedMessages.size()][];
        for (int i = 0; i < decodedMessages.size(); i++) {
            results[i] = decodedMessages.get(i);
        }
        boolean[] result = GFG.mergeBooleanArray(results);
        byte[] bytes = GFG.booleanArrayToByteArray(result);
        compareByteArrays(bytes, byteArray);
        countSuccessCounters(successCounters);
        System.out.println("Tyle % czasu kanał przebywał w stanie dobrym: " + GilbertElliottChannel.getGoodStatePercentage());
        System.out.println("Tyle % czasu kanał przebywał w stanie złym: " + GilbertElliottChannel.getBadStatePercentage());

        ByteArrayInputStream inStreambj = new ByteArrayInputStream(bytes);
        BufferedImage newImage = ImageIO.read(inStreambj);

        ImageIO.write(newImage, "BMP", new File("outputImage.bmp"));
        System.out.println("Image generated from the byte array.");

    }

    public static void compareByteArrays(byte[] array1, byte[] array2) {
        int differences = 0;

        // Divide the arrays into packages of 16 bytes
        int numPackages = Math.min(array1.length / 16, array2.length / 16);

        for (int i = 0; i < numPackages; i++) {
            // Get the package indexes
            int startIndex = i * 16;
            int endIndex = startIndex + 16;

            // Get the package slices from the arrays
            byte[] package1 = Arrays.copyOfRange(array1, startIndex, endIndex);
            byte[] package2 = Arrays.copyOfRange(array2, startIndex, endIndex);

            // Compare the packages
            if (!Arrays.equals(package1, package2)) {
                differences++;
            }
        }
        System.out.println("Ilość pakietów przepuszczonych z błedem: " + differences);
    }

    public static void countSuccessCounters(int[] successCounters) {
        int[] counts = new int[11];
        int otherCount = 0;
        for (int successCounter : successCounters) {
            if (successCounter >= 1 && successCounter <= 10) {
                counts[successCounter]++;
            } else {
                otherCount++;
            }
        }
        System.out.println("Statystyka pakietow przesylanych za X razem:");
        for (int i = 1; i <= 10; i++) {
            System.out.println(i + ": " + counts[i]);
        }
        System.out.println("Pakiety przeslane powyzej 10 razy: " + (otherCount - 2));
    }
}

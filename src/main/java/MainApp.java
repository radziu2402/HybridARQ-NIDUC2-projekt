import channels.BinarySymmetricChannel;
import channels.Channel;
import channels.GilbertElliottChannel;
import codes.*;
import com.casualcoding.reedsolomon.EncoderDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import messages.MessagePrinter;
import messages.GFG;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
        System.out.println("5. Kod Hamminga");
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
        } else if (errorDetectionCodeChoice == 5) {
            errorDetectionCode = new HammingCode(8);
        } else {
            System.out.println("Niepoprawny wybór kodu detekcyjnego.");
            return;
        }

        // Wybór kodu korekcyjnego
        System.out.println("Wybierz kod korekcyjny: ");
        System.out.println("1. Kod Hamminga");
        System.out.println("2. Kod RS");

        int errorCorrectionCodeChoice = scanner.nextInt();

        ErrorCorrectionCode errorCorrectionCode = null;
        EncoderDecoder RSencoderDecoder = null;
        if (errorCorrectionCodeChoice == 1) {
            //errorCorrectionCode = new HammingCode(16);
        } else if (errorCorrectionCodeChoice == 2) {
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
            System.out.println("Podaj wartość pierwszego parametru dla kanału(errorProbability):");
            double channelParam = scanner.nextDouble();
            System.out.println("Podaj wartość drugiego parametru dla kanału(switchProbability):");
            double secondChannelParam = scanner.nextDouble();
            System.out.println("Podaj wartość trzeciego parametru dla kanału(burstProbability):");
            double thirdChannelParam = scanner.nextDouble();
            channel = new GilbertElliottChannel(channelParam, secondChannelParam, thirdChannelParam);
        }

        BufferedImage image = ImageIO.read(new File("C:\\Users\\radziu2402\\Desktop\\sample.bmp"));
        // create the object of ByteArrayOutputStream class
        ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
        // write the image into the object of ByteArrayOutputStream class
        ImageIO.write(image, "BMP", outStreamObj);

        // create the byte array from image
        byte[] byteArray = outStreamObj.toByteArray();
        boolean[] message = GFG.byteArrayToBooleanArray(byteArray);
//        boolean[] message = {true, false, true, true, false, false, false, true, true, false, true, true, false, false, false, true, true, false, true, true, false, false, false, true, true, false, true, true, false, false, false, true};
        boolean[][] originalMessage = GFG.divideBooleanArray(message);
        ArrayList<boolean[]> decodedMessages = new ArrayList<>();
        int k = 1;
        for (boolean[] booleans : originalMessage) {
            System.out.println("Pakiet nr " + k);
            System.out.println("Wiadomość oryginalna:");
            MessagePrinter.printMessage(booleans);
            k++;
            boolean[] detectionEncodedMessage = errorDetectionCode.encode(booleans);
            System.out.println("Wiadomość zakodowana kodem detekcyjnym:");
            MessagePrinter.printMessage(detectionEncodedMessage);

            boolean[] receivedMessage = channel.transmit(detectionEncodedMessage);
            System.out.println("Wiadomość odebrana:");
            MessagePrinter.printMessage(receivedMessage);

            boolean[] detectionDecodedMessage = errorDetectionCode.decode(receivedMessage);
            if (detectionDecodedMessage != null) {
                System.out.println("Wiadomość zdekodowana poprawnie");
                MessagePrinter.printMessage(detectionDecodedMessage);
                decodedMessages.add(detectionDecodedMessage);
            } else {
                System.out.println("Wystąpił błąd w transmisji, dosyłam część korekcyjna");
                byte[] bytes = GFG.booleanArrayToByteArray(booleans);
                byte[] encode = RSencoderDecoder.encodeData(bytes, 3);
                boolean[] booleans1 = GFG.byteArrayToBooleanArray(encode);
                System.out.println("Wiadomość zakodowana korekcyjnym:");
                MessagePrinter.printMessage(booleans1);
                boolean[] receivedMessage1 = channel.transmit(booleans1);
                System.out.println("Wiadomość odebrana:");
                MessagePrinter.printMessage(receivedMessage1);
                byte[] bytes1 = GFG.booleanArrayToByteArray(receivedMessage1);
                byte[] correctionDecodedMessage = RSencoderDecoder.decodeData(bytes1, 3);
                boolean[] receivedMessage2;
                while (correctionDecodedMessage == null) {
                    System.out.println("Za duzo bledow w czesci korekcyjnej wiec wysylamy ja znowu");
                    receivedMessage2 = channel.transmit(booleans1);
                    System.out.println("Wiadomość odebrana:");
                    MessagePrinter.printMessage(receivedMessage2);
                    bytes1 = GFG.booleanArrayToByteArray(receivedMessage2);
                    correctionDecodedMessage = RSencoderDecoder.decodeData(bytes1, 3);
                }
                boolean[] booleans2 = GFG.byteArrayToBooleanArray(correctionDecodedMessage);
                System.out.println("Wiadomość zdekodowana poprawnie");
                MessagePrinter.printMessage(booleans2);
                decodedMessages.add(booleans2);
            }
            System.out.println(" ");
            System.out.println(" ");
        }
        boolean[][] results = new boolean[decodedMessages.size()][];
        for (int i = 0; i < decodedMessages.size(); i++) {
            results[i] = decodedMessages.get(i);
        }
        boolean[] result = GFG.mergeBooleanArray(results);
        byte[] bytes = GFG.booleanArrayToByteArray(result);
        ByteArrayInputStream inStreambj = new ByteArrayInputStream(bytes);
        BufferedImage newImage = ImageIO.read(inStreambj);

        ImageIO.write(newImage, "BMP", new File("outputImage.bmp"));
        System.out.println("Image generated from the byte array.");
        compareBooleanArrays(result, message);
    }

    public static void compareBooleanArrays(boolean[] array1, boolean[] array2) {
        int differences = 0;
        if (array1.length != array2.length) {
            throw new IllegalArgumentException("Arrays must have the same length");
        } else {
            for (int i = 0; i < array1.length; i++) {
                if (array1[i] != array2[i]) {
                    differences++;
                }
            }
        }
        System.out.println("Arrays differ in " + differences + " places.");
    }
}
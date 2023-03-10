package messages;

public class MessagePrinter {
    public static void printMessage(boolean[] message) {
        for (boolean bit : message) {
            System.out.print(bit ? "1" : "0");
        }
        System.out.println();
    }
}
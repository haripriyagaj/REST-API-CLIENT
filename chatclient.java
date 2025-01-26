import java.io.*;
import java.net.*;
import java.util.Scanner;

public class chatclient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);
        System.out.println("Connected to the server!");

        new Thread(new ReceiveMessage(socket)).start();
        new Thread(new SendMessage(socket)).start();
    }
}

class ReceiveMessage implements Runnable {
    private Socket socket;

    public ReceiveMessage(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            while (true) {
                if (in.hasNextLine()) {
                    System.out.println("Server: " + in.nextLine());
                }
            }
        } catch (IOException e) {
            System.err.println("Error receiving message: " + e.getMessage());
        }
    }
}

class SendMessage implements Runnable {
    private Socket socket;

    public SendMessage(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                out.println(message);
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}

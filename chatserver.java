import java.io.*;
import java.net.*;
import java.util.*;

public class chatserver {
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Server is running...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New client connected!");
            ClientHandler clientHandler = new ClientHandler(socket, clientHandlers);
            clientHandlers.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private static Set<ClientHandler> clientHandlers;

    public ClientHandler(Socket socket, Set<ClientHandler> clientHandlers) {
        this.socket = socket;
        ClientHandler.clientHandlers = clientHandlers;
    }

    @Override
    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String message = in.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                broadcastMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Error in client communication: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : clientHandlers) {
            if (client != this) {
                client.out.println(message);
            }
        }
    }

    private void closeConnection() {
        try {
            clientHandlers.remove(this);
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}

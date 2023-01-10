import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private ServerSocket serverSocket;
    private List<PrintWriter> clientWriters;

    public ChatServer(int port) {
        try {
            // Create a new server socket
            serverSocket = new ServerSocket(port);
            clientWriters = new ArrayList<>();

            // Continuously listen for new clients
            while (true) {
                // Accept a new client
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                // Create a new thread for the client
                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            try {
                // Initialize the input and output streams
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String message;
            try {
                // Continuously listen for messages from the client
                while ((message = in.readLine()) != null) {
                    System.out.println("Received message: " + message);
                    // Broadcast the message to all connected clients
                    for (PrintWriter writer : clientWriters) {
                        writer.println(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        ChatServer chatServer = new ChatServer(port);
    }
}

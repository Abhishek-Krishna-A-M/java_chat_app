package client;

import model.Message;
import model.User;
import utils.Constants;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientNetwork {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private User currentUser;
    private final BlockingQueue<Message> incomingMessages;
    private volatile boolean connected;
    private String serverHost;
    private int serverPort;

    public ClientNetwork(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        incomingMessages = new LinkedBlockingQueue<>();
        connected = false;
    }

    public boolean connect(User user) {
        try {
            System.out.println("🔗 Connecting to " + serverHost + ":" + serverPort + "...");
            socket = new Socket(serverHost, serverPort);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            currentUser = user;
            connected = true;

            // Send user information to server
            output.writeObject(user);
            output.flush();

            System.out.println("✅ Connected successfully as: " + user.getUsername());

            // Start listening for incoming messages
            Thread listenerThread = new Thread(this::listenForMessages);
            listenerThread.setDaemon(true);
            listenerThread.start();

            return true;

        } catch (IOException e) {
            System.out.println("❌ Failed to connect to server: " + e.getMessage());
            return false;
        }
    }

    private void listenForMessages() {
        try {
            while (connected && !socket.isClosed()) {
                Message message = (Message) input.readObject();
                if (message != null) {
                    incomingMessages.put(message);
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            if (connected) {
                System.out.println("Connection lost: " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }

    public void sendMessage(Message message) {
        if (connected && output != null) {
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException e) {
                System.out.println("Failed to send message: " + e.getMessage());
                disconnect();
            }
        }
    }

    public Message getNextMessage() throws InterruptedException {
        return incomingMessages.take();
    }

    public void disconnect() {
        connected = false;
        try {
            if (input != null)
                input.close();
            if (output != null)
                output.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            System.out.println("Error during disconnect");
        }

        // Add a system message indicating disconnect
        try {
            incomingMessages.put(new Message(currentUser, "Disconnected from server", "SYSTEM"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
}

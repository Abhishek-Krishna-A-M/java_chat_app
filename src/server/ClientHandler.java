package server;

import model.Message;
import model.User;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private User user;
    private BlockingQueue<Message> messageQueue;

    public ClientHandler(Socket socket, ChatServer server, BlockingQueue<Message> messageQueue) {
        this.socket = socket;
        this.server = server;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            // Read user information
            user = (User) input.readObject();
            System.out.println("User connected: " + user.getUsername());

            // Add user to server's user list
            server.addUser(user);

            // Notify all clients about new user
            Message joinMessage = new Message(user, user.getUsername() + " joined the chat", "USER_JOIN");
            messageQueue.put(joinMessage);

            // Start listening for messages
            while (!socket.isClosed()) {
                try {
                    Message message = (Message) input.readObject();
                    if (message != null) {
                        messageQueue.put(message);
                    }
                } catch (EOFException e) {
                    break;
                }
            }

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.out.println("Client handler error for " + (user != null ? user.getUsername() : "unknown"));
        } finally {
            disconnect();
        }
    }

    public void sendMessage(Message message) {
        try {
            if (output != null) {
                output.writeObject(message);
                output.flush();
            }
        } catch (IOException e) {
            System.out.println("Error sending message to " + user.getUsername());
            disconnect();
        }
    }

    private void disconnect() {
        try {
            if (user != null) {
                System.out.println("User disconnected: " + user.getUsername());

                // Notify about user leaving
                try {
                    Message leaveMessage = new Message(user, user.getUsername() + " left the chat", "USER_LEAVE");
                    messageQueue.put(leaveMessage);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                server.removeClient(this);
            }

            if (input != null)
                input.close();
            if (output != null)
                output.close();
            if (socket != null)
                socket.close();

        } catch (IOException e) {
            System.out.println("Error during client disconnect");
        }
    }

    public User getUser() {
        return user;
    }
}

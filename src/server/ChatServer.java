package server;

import model.Message;
import model.User;
import utils.Constants;
import utils.NetworkUtils;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatServer {
    private ServerSocket serverSocket;
    private final CopyOnWriteArrayList<ClientHandler> clients;
    private final CopyOnWriteArrayList<User> connectedUsers;
    private final BlockingQueue<Message> messageQueue;
    private final ExecutorService clientExecutor;
    private final AtomicBoolean isRunning;
    
    public ChatServer() {
        clients = new CopyOnWriteArrayList<>();
        connectedUsers = new CopyOnWriteArrayList<>();
        messageQueue = new LinkedBlockingQueue<>();
        clientExecutor = Executors.newCachedThreadPool();
        isRunning = new AtomicBoolean(false);
    }
    
    public void start() {
        try {
            // Display network information
            NetworkUtils.displayNetworkInfo();
            
            // Create server socket that listens on all interfaces
            serverSocket = new ServerSocket(Constants.SERVER_PORT, 50, 
                java.net.InetAddress.getByName(Constants.SERVER_HOST));
            
            isRunning.set(true);
            System.out.println("🚀 Chat server started successfully!");
            System.out.println("📍 Listening on port: " + Constants.SERVER_PORT);
            System.out.println("🌐 Accessible from other devices on your network");
            System.out.println("⏹️  Press Ctrl+C to stop the server");
            
            // Start message broadcaster
            Thread broadcasterThread = new Thread(this::broadcastMessages);
            broadcasterThread.setDaemon(true);
            broadcasterThread.start();
            
            // Accept client connections
            while (isRunning.get()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    String clientAddress = clientSocket.getInetAddress().getHostAddress();
                    System.out.println("📱 New connection from: " + clientAddress);
                    
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this, messageQueue);
                    clients.add(clientHandler);
                    clientExecutor.execute(clientHandler);
                    
                    System.out.println("👥 Total connected clients: " + clients.size());
                    
                } catch (IOException e) {
                    if (isRunning.get()) {
                        System.out.println("❌ Error accepting client connection: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.out.println("❌ Could not start server on port " + Constants.SERVER_PORT);
            System.out.println("💡 Error: " + e.getMessage());
            System.out.println("💡 Make sure the port is not already in use");
        } finally {
            stop();
        }
    }
    
    private void broadcastMessages() {
        while (isRunning.get()) {
            try {
                Message message = messageQueue.take();
                System.out.println("📨 Broadcasting: " + message.getDisplayText());
                
                // Send message to all clients
                for (ClientHandler client : clients) {
                    client.sendMessage(message);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public void addUser(User user) {
        connectedUsers.add(user);
        System.out.println("➕ User joined: " + user.getUsername());
        System.out.println("👥 Total users: " + connectedUsers.size());
        
        // Broadcast updated user list to all clients
        broadcastUserList();
    }
    
    public void removeUser(User user) {
        connectedUsers.remove(user);
        System.out.println("➖ User left: " + user.getUsername());
        System.out.println("👥 Total users: " + connectedUsers.size());
        
        // Broadcast updated user list to all clients
        broadcastUserList();
    }
    
    private void broadcastUserList() {
        // Create a system message with user list update
        StringBuilder userListBuilder = new StringBuilder();
        userListBuilder.append("USER_LIST_UPDATE:");
        for (User user : connectedUsers) {
            userListBuilder.append(user.getUsername()).append(",");
        }
        
        // Remove trailing comma
        if (userListBuilder.length() > 0) {
            userListBuilder.setLength(userListBuilder.length() - 1);
        }
        
        Message userListMessage = new Message(new User("System"), userListBuilder.toString(), "SYSTEM");
        
        // Send to all clients
        for (ClientHandler client : clients) {
            client.sendMessage(userListMessage);
        }
    }
    
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        if (client.getUser() != null) {
            removeUser(client.getUser());
        }
        System.out.println("➖ Client disconnected. Total clients: " + clients.size());
    }
    
    public void stop() {
        isRunning.set(false);
        clientExecutor.shutdown();
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server socket");
        }
        
        System.out.println("🛑 Chat server stopped");
    }
    
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        
        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🛑 Shutting down server...");
            server.stop();
        }));
        
        server.start();
    }
}

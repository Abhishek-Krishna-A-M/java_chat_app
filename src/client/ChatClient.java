package client;

import gui.ChatFrame;
import gui.ConnectionDialog;
import model.User;
import utils.Constants;
import javax.swing.*;

public class ChatClient {
    public static void main(String[] args) {
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            // Show connection dialog first
            ConnectionDialog connectionDialog = new ConnectionDialog(null);
            connectionDialog.setVisible(true);

            if (connectionDialog.isConnected()) {
                User user = connectionDialog.getUser();
                String serverHost = connectionDialog.getServerHost();
                int serverPort = connectionDialog.getServerPort();

                ClientNetwork clientNetwork = new ClientNetwork(serverHost, serverPort);

                if (clientNetwork.connect(user)) {
                    // Create and show main chat frame
                    ChatFrame chatFrame = new ChatFrame(user, clientNetwork);
                    chatFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Failed to connect to server at " + serverHost + ":" + serverPort + "\n" +
                                    "Make sure:\n" +
                                    "• The server is running\n" +
                                    "• The IP address is correct\n" +
                                    "• Both devices are on the same network\n" +
                                    "• Firewall allows the connection",
                            "Connection Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}

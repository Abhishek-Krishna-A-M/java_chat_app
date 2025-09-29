package gui;

import model.User;
import utils.Constants;
import utils.NetworkUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ConnectionDialog extends JDialog {
    private JTextField usernameField;
    private JTextField serverHostField;
    private JTextField serverPortField;
    private JButton connectButton;
    private JButton detectButton;
    private User user;
    private boolean connected;
    private String serverHost;
    private int serverPort;

    public ConnectionDialog(Frame parent) {
        super(parent, "Connect to Chat", true);
        this.connected = false;
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        applyModernStyling();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        usernameField.setText("User" + System.currentTimeMillis() % 1000);

        serverHostField = new JTextField(15);
        serverHostField.setText("localhost"); // Default to localhost

        serverPortField = new JTextField(8);
        serverPortField.setText(String.valueOf(Constants.SERVER_PORT));

        connectButton = new JButton("Connect");
        connectButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        detectButton = new JButton("Detect Network");
        detectButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBackground(Constants.CHAT_BACKGROUND);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Constants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("ChatApp - Connect", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Constants.TEXT_PRIMARY);
        mainPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(createStyledTextField(usernameField), gbc);

        // Server Host
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel hostLabel = new JLabel("Server IP:");
        hostLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        hostLabel.setForeground(Constants.TEXT_PRIMARY);
        mainPanel.add(hostLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(createStyledTextField(serverHostField), gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(createStyledButton(detectButton, false), gbc);

        // Server Port
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel portLabel = new JLabel("Port:");
        portLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        portLabel.setForeground(Constants.TEXT_PRIMARY);
        mainPanel.add(portLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(createStyledTextField(serverPortField), gbc);

        // Connect button
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 10, 0, 10);
        mainPanel.add(createStyledButton(connectButton, true), gbc);

        add(mainPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private JTextField createStyledTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JButton createStyledButton(JButton button, boolean isPrimary) {
        if (isPrimary) {
            button.setBackground(Constants.PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setBorder(BorderFactory.createEmptyBorder(12, 35, 12, 35));
        } else {
            button.setBackground(Constants.SECONDARY_COLOR);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        }

        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Constants.ACCENT_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(Constants.PRIMARY_COLOR);
                } else {
                    button.setBackground(Constants.SECONDARY_COLOR);
                }
            }
        });

        return button;
    }

    private void applyModernStyling() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting modern look and feel: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        connectButton.addActionListener(this::connect);
        detectButton.addActionListener(this::detectNetwork);

        usernameField.addActionListener(this::connect);
        serverHostField.addActionListener(this::connect);
        serverPortField.addActionListener(this::connect);
    }

    private void connect(ActionEvent e) {
        String username = usernameField.getText().trim();
        String host = serverHostField.getText().trim();
        String portText = serverPortField.getText().trim();

        if (username.isEmpty()) {
            showErrorDialog("Please enter a username");
            return;
        }

        if (host.isEmpty()) {
            showErrorDialog("Please enter server IP address");
            return;
        }

        if (portText.isEmpty()) {
            showErrorDialog("Please enter server port");
            return;
        }

        try {
            serverPort = Integer.parseInt(portText);
            if (serverPort < 1 || serverPort > 65535) {
                showErrorDialog("Port must be between 1 and 65535");
                return;
            }
        } catch (NumberFormatException ex) {
            showErrorDialog("Invalid port number");
            return;
        }

        if (username.length() > 20) {
            showErrorDialog("Username must be 20 characters or less");
            return;
        }

        user = new User(username);
        serverHost = host;
        connected = true;
        dispose();
    }

    private void detectNetwork(ActionEvent e) {
        String localIP = NetworkUtils.getLocalIP();
        serverHostField.setText(localIP);

        JOptionPane.showMessageDialog(this,
                "Local IP detected: " + localIP + "\n\n" +
                        "If you're running the server on this device,\n" +
                        "other devices should connect to this IP address.",
                "Network Detection",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public User getUser() {
        return user;
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public boolean isConnected() {
        return connected;
    }
}

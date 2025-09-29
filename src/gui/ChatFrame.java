package gui;

import client.ClientNetwork;
import model.Message;
import model.User;
import utils.Constants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatFrame extends JFrame {
    private ClientNetwork clientNetwork;
    private User currentUser;

    private MessagePanel messagePanel;
    private JTextField messageField;
    private JButton sendButton;
    private JButton attachButton;
    private JLabel statusLabel;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JLabel onlineCountLabel;

    public ChatFrame(User user, ClientNetwork clientNetwork) {
        this.currentUser = user;
        this.clientNetwork = clientNetwork;

        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        applyModernStyling();
        startMessageListener();

        setTitle("ChatApp • " + user.getUsername());
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void initializeComponents() {
        // Message panel
        messagePanel = new MessagePanel(currentUser);

        // Input components
        messageField = new JTextField();
        messageField.setToolTipText("Type a message...");

        sendButton = new JButton("Send");
        attachButton = new JButton("📎");
        attachButton.setToolTipText("Attach file");

        // Status label
        statusLabel = new JLabel("Online • " + currentUser.getUsername());
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // User list
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userList.setBackground(Constants.SIDEBAR_BACKGROUND);

        onlineCountLabel = new JLabel("Online • 1");
        onlineCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        onlineCountLabel.setForeground(Constants.TEXT_SECONDARY);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Constants.CHAT_BACKGROUND);

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main content
        add(createMainPanel(), BorderLayout.CENTER);

        // Input panel
        add(createInputPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.HEADER_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Constants.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel titleLabel = new JLabel("ChatApp");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Constants.PRIMARY_COLOR);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        userPanel.add(statusLabel);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Constants.CHAT_BACKGROUND);

        // Messages area with reduced margins
        JPanel messageWrapper = new JPanel(new BorderLayout());
        messageWrapper.setBackground(Constants.CHAT_BACKGROUND);
        messageWrapper.setBorder(new EmptyBorder(5, 5, 5, 5));
        messageWrapper.add(messagePanel, BorderLayout.CENTER);

        mainPanel.add(messageWrapper, BorderLayout.CENTER);

        // Online users sidebar
        mainPanel.add(createSidebarPanel(), BorderLayout.EAST);

        return mainPanel;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(Constants.SIDEBAR_BACKGROUND);
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Constants.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)));

        JLabel sidebarTitle = new JLabel("Online Users");
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sidebarTitle.setForeground(Constants.TEXT_PRIMARY);
        sidebarTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setBorder(BorderFactory.createEmptyBorder());
        userScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        sidebarPanel.add(sidebarTitle, BorderLayout.NORTH);
        sidebarPanel.add(userScrollPane, BorderLayout.CENTER);
        sidebarPanel.add(onlineCountLabel, BorderLayout.SOUTH);

        return sidebarPanel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Constants.HEADER_BACKGROUND);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Constants.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // Style message field
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Style attach button
        attachButton.setBackground(Color.WHITE);
        attachButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        attachButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Style send button
        sendButton.setBackground(Constants.PRIMARY_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect for send button
        sendButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sendButton.setBackground(Constants.SECONDARY_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                sendButton.setBackground(Constants.PRIMARY_COLOR);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(attachButton);
        buttonPanel.add(sendButton);

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        return inputPanel;
    }

    private void applyModernStyling() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Set modern UI defaults
            UIManager.put("ScrollBar.thumb", Constants.PRIMARY_COLOR);
            UIManager.put("ScrollBar.thumbDarkShadow", Constants.PRIMARY_COLOR);
            UIManager.put("ScrollBar.thumbHighlight", Constants.SECONDARY_COLOR);

        } catch (Exception e) {
            System.out.println("Error setting modern look and feel: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        sendButton.addActionListener(this::sendMessage);
        messageField.addActionListener(this::sendMessage);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
            }
        });
    }

    private void sendMessage(ActionEvent e) {
        String text = messageField.getText().trim();

        if (!text.isEmpty() && clientNetwork.isConnected()) {
            Message message = new Message(currentUser, text, "TEXT");
            clientNetwork.sendMessage(message);
            messageField.setText("");
        }
    }

    private void startMessageListener() {
        Thread messageListener = new Thread(() -> {
            while (clientNetwork.isConnected()) {
                try {
                    Message message = clientNetwork.getNextMessage();
                    messagePanel.addMessage(message);

                    SwingUtilities.invokeLater(() -> {
                        updateUserList(message);
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            SwingUtilities.invokeLater(() -> {
                if (!clientNetwork.isConnected()) {
                    JOptionPane.showMessageDialog(this,
                            "Disconnected from server",
                            "Connection Lost",
                            JOptionPane.WARNING_MESSAGE);
                    disconnect();
                }
            });
        });

        messageListener.setDaemon(true);
        messageListener.start();
    }

    private void updateUserList(Message message) {
        String username = message.getSender().getUsername();

        switch (message.getMessageType()) {
            case "USER_JOIN":
                if (!userListModel.contains(username)) {
                    userListModel.addElement(username);
                    updateOnlineCount();
                }
                break;
            case "USER_LEAVE":
                userListModel.removeElement(username);
                updateOnlineCount();
                break;
        }
    }

    private void updateOnlineCount() {
        onlineCountLabel.setText("Online • " + userListModel.size());
    }

    private void disconnect() {
        if (clientNetwork != null) {
            clientNetwork.disconnect();
        }
        dispose();
        System.exit(0);
    }
}

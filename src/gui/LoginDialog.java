package gui;

import model.User;
import utils.Constants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JButton connectButton;
    private User user;
    private boolean connected;

    public LoginDialog(Frame parent) {
        super(parent, "Join ChatApp", true);
        this.connected = false;
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        applyModernStyling();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        usernameField.setText("User" + System.currentTimeMillis() % 1000);

        connectButton = new JButton("Join Chat");
        connectButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBackground(Constants.CHAT_BACKGROUND);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Constants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("ChatApp", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("Choose your username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Constants.TEXT_PRIMARY);
        mainPanel.add(userLabel, gbc);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        mainPanel.add(createStyledTextField(usernameField), gbc);

        // Connect button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(25, 15, 0, 15);
        mainPanel.add(createStyledButton(connectButton), gbc);

        add(mainPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private JTextField createStyledTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JButton createStyledButton(JButton button) {
        button.setBackground(Constants.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Constants.SECONDARY_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Constants.PRIMARY_COLOR);
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
        usernameField.addActionListener(this::connect);
    }

    private void connect(ActionEvent e) {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            showErrorDialog("Please enter a username");
            return;
        }

        if (username.length() > 20) {
            showErrorDialog("Username must be 20 characters or less");
            return;
        }

        user = new User(username);
        connected = true;
        dispose();
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public User getUser() {
        return user;
    }

    public boolean isConnected() {
        return connected;
    }
}

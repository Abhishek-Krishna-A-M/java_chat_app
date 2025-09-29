package gui;

import model.Message;
import model.User;
import utils.Constants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class MessagePanel extends JPanel {
    private JPanel messagesContainer;
    private User currentUser;
    
    public MessagePanel(User currentUser) {
        this.currentUser = currentUser;
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(Constants.CHAT_BACKGROUND);
        
        messagesContainer = new JPanel();
        messagesContainer.setLayout(new BoxLayout(messagesContainer, BoxLayout.Y_AXIS));
        messagesContainer.setBackground(Constants.CHAT_BACKGROUND);
        messagesContainer.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(messagesContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void addMessage(Message message) {
        SwingUtilities.invokeLater(() -> {
            boolean isOwnMessage = message.getSender().getUsername().equals(currentUser.getUsername());
            boolean isSystemMessage = message.getMessageType().equals("SYSTEM") || 
                                    message.getMessageType().equals("USER_JOIN") || 
                                    message.getMessageType().equals("USER_LEAVE");
            
            JPanel messageBubble = createMessageBubble(message, isOwnMessage, isSystemMessage);
            messagesContainer.add(messageBubble);
            messagesContainer.revalidate();
            messagesContainer.repaint();
            
            // Auto-scroll to bottom
            JViewport viewport = (JViewport) messagesContainer.getParent().getParent();
            viewport.setViewPosition(new Point(0, messagesContainer.getHeight()));
        });
    }
    
    private JPanel createMessageBubble(Message message, boolean isOwnMessage, boolean isSystemMessage) {
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BorderLayout());
        bubblePanel.setOpaque(false);
        bubblePanel.setBorder(new EmptyBorder(2, 8, 2, 8)); // Reduced padding
        
        if (isSystemMessage) {
            JLabel systemLabel = new JLabel(message.getDisplayText(), JLabel.CENTER);
            systemLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            systemLabel.setForeground(Constants.TEXT_SECONDARY);
            systemLabel.setOpaque(false);
            bubblePanel.add(systemLabel, BorderLayout.CENTER);
            return bubblePanel;
        }
        
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout(4, 2)); // Reduced gaps
        messagePanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12)); // Reduced padding
        
        // Set maximum width for message bubbles
        messagePanel.setMaximumSize(new Dimension(280, Integer.MAX_VALUE));
        
        if (isOwnMessage) {
            messagePanel.setBackground(Constants.MESSAGE_OUTGOING);
        } else {
            messagePanel.setBackground(Constants.MESSAGE_INCOMING);
            messagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
        }
        
        // Rounded corners
        messagePanel.setBorder(new RoundedBorder(18, 
            isOwnMessage ? Constants.MESSAGE_OUTGOING : Constants.MESSAGE_INCOMING));
        
        // Message content
        JTextArea contentArea = new JTextArea(message.getContent());
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentArea.setForeground(isOwnMessage ? Constants.TEXT_WHITE : Constants.TEXT_PRIMARY);
        contentArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        
        messagePanel.add(contentArea, BorderLayout.CENTER);
        
        // Timestamp and sender
        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        metaPanel.setOpaque(false);
        
        if (!isOwnMessage) {
            JLabel senderLabel = new JLabel(message.getSender().getUsername());
            senderLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            senderLabel.setForeground(Constants.TEXT_ACCENT);
            metaPanel.add(senderLabel);
        }
        
        JLabel timeLabel = new JLabel(message.getFormattedTimestamp());
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        timeLabel.setForeground(isOwnMessage ? new Color(255, 255, 255, 180) : 
                                              new Color(100, 116, 139, 180));
        metaPanel.add(timeLabel);
        
        messagePanel.add(metaPanel, BorderLayout.SOUTH);
        
        // Align messages properly
        JPanel alignmentPanel = new JPanel(new FlowLayout(
            isOwnMessage ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        alignmentPanel.setOpaque(false);
        alignmentPanel.add(messagePanel);
        
        bubblePanel.add(alignmentPanel, BorderLayout.CENTER);
        
        return bubblePanel;
    }
    
    public void clear() {
        SwingUtilities.invokeLater(() -> {
            messagesContainer.removeAll();
            messagesContainer.revalidate();
            messagesContainer.repaint();
        });
    }
    
    // Rounded border class
    private static class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        private Color color;
        
        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius/2, this.radius/2, this.radius/2, this.radius/2);
        }
        
        public boolean isBorderOpaque() {
            return true;
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }
}

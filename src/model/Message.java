package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private User sender;
    private String content;
    private LocalDateTime timestamp;
    private String messageType;

    public Message(User sender, String content, String messageType) {
        this.sender = sender;
        this.content = content;
        this.messageType = messageType;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public User getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return timestamp.format(formatter);
    }

    public String getDisplayText() {
        switch (messageType) {
            case "SYSTEM":
                return "[System] " + content;
            case "USER_JOIN":
                return "→ " + sender.getUsername() + " joined the chat";
            case "USER_LEAVE":
                return "← " + sender.getUsername() + " left the chat";
            default:
                return "[" + sender.getUsername() + "]: " + content;
        }
    }
}

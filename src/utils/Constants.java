package utils;

import java.awt.*;

public class Constants {
    public static final String SERVER_HOST = "0.0.0.0";
    public static final int SERVER_PORT = 12345;
    public static final int MAX_MESSAGE_LENGTH = 1000;

    // Message types
    public static final String MESSAGE_TYPE_TEXT = "TEXT";
    public static final String MESSAGE_TYPE_SYSTEM = "SYSTEM";
    public static final String MESSAGE_TYPE_USER_JOIN = "USER_JOIN";
    public static final String MESSAGE_TYPE_USER_LEAVE = "USER_LEAVE";

    // UI Constants
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;

    // Modern purple theme colors (Instagram-like)
    public static final Color PRIMARY_COLOR = new Color(147, 51, 234); // Purple
    public static final Color SECONDARY_COLOR = new Color(192, 132, 252); // Light purple
    public static final Color ACCENT_COLOR = new Color(236, 72, 153); // Pink accent

    public static final Color CHAT_BACKGROUND = new Color(250, 250, 250);
    public static final Color MESSAGE_INCOMING = new Color(255, 255, 255);
    public static final Color MESSAGE_OUTGOING = new Color(147, 51, 234);
    public static final Color HEADER_BACKGROUND = new Color(255, 255, 255);
    public static final Color SIDEBAR_BACKGROUND = new Color(248, 250, 252);

    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    public static final Color TEXT_WHITE = new Color(255, 255, 255);
    public static final Color TEXT_ACCENT = new Color(236, 72, 153);

    public static final Color BORDER_LIGHT = new Color(226, 232, 240);
    public static final Color HOVER_EFFECT = new Color(243, 244, 246);
}

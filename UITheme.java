import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class UITheme {
    // Define theme colors
    public static final Color PRIMARY_COLOR = new Color(255, 122, 0);
    public static final Color SECONDARY_COLOR = new Color(245, 245, 245);
    public static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    public static final Color TEXT_COLOR = new Color(33, 33, 33);
    public static final Color BORDER_COLOR = new Color(224, 224, 224);
    
    // Define fonts
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 12);
    
    // Define borders
    public static final Border PANEL_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
    );
    

    public static void applyTheme() {
        // Set default UI properties
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("Button.background", PRIMARY_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", NORMAL_FONT);
        UIManager.put("Label.font", NORMAL_FONT);
        UIManager.put("TextField.font", NORMAL_FONT);
        UIManager.put("PasswordField.font", NORMAL_FONT);
        UIManager.put("TextArea.font", NORMAL_FONT);
        UIManager.put("ComboBox.font", NORMAL_FONT);
        UIManager.put("Table.font", NORMAL_FONT);
        UIManager.put("TableHeader.font", HEADER_FONT);
        UIManager.put("TabbedPane.font", HEADER_FONT);
        UIManager.put("TabbedPane.selected", PRIMARY_COLOR);
    }
    
    // Helper methods for consistent UI components
    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
    

    

    
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        return label;
    }
}


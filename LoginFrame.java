import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LoginFrame extends JFrame {
    private CafeManagementApp app;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    
    public LoginFrame(CafeManagementApp app) {
        this.app = app;
        
        // Set up the frame
        setTitle("Cafe Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create the main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);
        
        // Create the header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Cafe Management System");
        titleLabel.setFont(UITheme.TITLE_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Login to your account");
        subtitleLabel.setFont(UITheme.NORMAL_FONT);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        // Create the form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(UITheme.BACKGROUND_COLOR);
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(errorLabel);
        
        // Create the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.BACKGROUND_COLOR);
        
        JButton loginButton = UITheme.createButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        buttonPanel.add(loginButton);
        
        // Add components to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set the content pane
        setContentPane(mainPanel);
        
        // Set default button for Enter key
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }
        
        List<User> users = FileHandler.loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                app.login(user);
                return;
            }
        }
        
        errorLabel.setText("Invalid username or password");
    }
}


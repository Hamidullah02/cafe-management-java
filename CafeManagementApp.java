import javax.swing.*;


public class CafeManagementApp {
    private JFrame mainFrame;
    private LoginFrame loginFrame;
    private MainDashboard dashboard;
    private User currentUser;

    public CafeManagementApp() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        UITheme.applyTheme();
        
        initialize();
    }

    private void initialize() {
        mainFrame = new JFrame("Cafe Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1024, 768);
        mainFrame.setLocationRelativeTo(null);
        
        loginFrame = new LoginFrame(this);
        loginFrame.setVisible(true);
    }

    public void login(User user) {
        this.currentUser = user;
        loginFrame.dispose();
        
        dashboard = new MainDashboard(this, currentUser);
        mainFrame.setContentPane(dashboard);
        mainFrame.setVisible(true);
    }

    public void logout() {
        this.currentUser = null;
        mainFrame.setVisible(false);
        
        loginFrame = new LoginFrame(this);
        loginFrame.setVisible(true);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CafeManagementApp();
        });
    }
}


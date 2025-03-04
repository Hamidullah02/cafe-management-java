import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainDashboard extends JPanel {
    private CafeManagementApp app;
    private User currentUser;
    private JTabbedPane tabbedPane;
    
    public MainDashboard(CafeManagementApp app, User currentUser) {
        this.app = app;
        this.currentUser = currentUser;
        
        // Set up the panel
        setLayout(new BorderLayout());
        
        // Create the header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Create the tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.HEADER_FONT);
        
        // Add tabs
        tabbedPane.addTab("Orders", new OrderPanel());
        tabbedPane.addTab("Menu", new MenuManagementPanel());
        tabbedPane.addTab("Sales", new SalesReportPanel());
        
        // Add User Management tab only for admin users
        if (currentUser.isAdmin()) {
            tabbedPane.addTab("Users", new UserManagementPanel());
        }
        
        // Create the footer panel
        JPanel footerPanel = createFooterPanel();
        
        // Add components to the main panel
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Cafe Management System");
        titleLabel.setFont(UITheme.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
        welcomeLabel.setFont(UITheme.NORMAL_FONT);
        welcomeLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(UITheme.SMALL_FONT);
        logoutButton.setBackground(UITheme.SECONDARY_COLOR);
        logoutButton.setForeground(UITheme.TEXT_COLOR);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.logout();
            }
        });
        
        userPanel.add(welcomeLabel);
        userPanel.add(logoutButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(UITheme.SECONDARY_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JLabel copyrightLabel = new JLabel("© " + java.time.Year.now().getValue() + " Cafe Management System");
        copyrightLabel.setFont(UITheme.SMALL_FONT);
        
        footerPanel.add(copyrightLabel);
        
        return footerPanel;
    }
}


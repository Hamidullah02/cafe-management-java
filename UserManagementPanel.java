import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable userTable;
    
    public UserManagementPanel() {
        // Set up the panel
        setLayout(new BorderLayout());
        setBorder(UITheme.PANEL_BORDER);
        
        // Create the header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Create the table panel
        JPanel tablePanel = createTablePanel();
        
        // Add components to the main panel
        add(headerPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        
        // Load users
        loadUsers();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = UITheme.createHeaderLabel("User Management");
        
        JButton addButton = UITheme.createButton("Add User");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddUserDialog();
            }
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        
        // Create table model with columns
        String[] columns = {"ID", "Username", "Role", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only actions column is editable
            }
        };
        
        // Create table
        userTable = new JTable(tableModel);
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        // Add button renderer and editor to the actions column
        userTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        userTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private void loadUsers() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Load users from file
        List<User> users = FileHandler.loadUsers();
        
        // Add users to the table
        for (User user : users) {
            Object[] row = {
                user.getId(),
                user.getUsername(),
                user.isAdmin() ? "Administrator" : "Staff",
                "Edit/Delete"
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add User", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        
        JLabel roleLabel = new JLabel("Role:");
        JCheckBox adminCheckBox = new JCheckBox("Administrator");
        
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(roleLabel);
        formPanel.add(adminCheckBox);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton addButton = UITheme.createButton("Add");
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean isAdmin = adminCheckBox.isSelected();
                
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                FileHandler.addUser(username, password, isAdmin);
                loadUsers();
                dialog.dispose();
                
                JOptionPane.showMessageDialog(dialog, "User added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditUserDialog(String id) {
        // Find the user
        List<User> users = FileHandler.loadUsers();
        User userToEdit = null;
        
        for (User user : users) {
            if (user.getId().equals(id)) {
                userToEdit = user;
                break;
            }
        }
        
        if (userToEdit == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(userToEdit.getUsername());
        
        JLabel passwordLabel = new JLabel("Password (leave blank to keep current):");
        JPasswordField passwordField = new JPasswordField();
        
        JLabel roleLabel = new JLabel("Role:");
        JCheckBox adminCheckBox = new JCheckBox("Administrator", userToEdit.isAdmin());
        
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(roleLabel);
        formPanel.add(adminCheckBox);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton updateButton = UITheme.createButton("Update");
        
        final User finalUserToEdit = userToEdit;
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean isAdmin = adminCheckBox.isSelected();
                
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                finalUserToEdit.setUsername(username);
                if (!password.isEmpty()) {
                    finalUserToEdit.setPassword(password);
                }
                finalUserToEdit.setAdmin(isAdmin);
                
                FileHandler.updateUser(finalUserToEdit);
                loadUsers();
                dialog.dispose();
                
                JOptionPane.showMessageDialog(dialog, "User updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(updateButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void deleteUser(String id) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this user?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            FileHandler.deleteUser(id);
            loadUsers();
            JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Button renderer for the actions column
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    // Button editor for the actions column
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String id;
        private boolean isPushed;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            id = (String) table.getValueAt(row, 0);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Show a popup menu with edit and delete options
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem editItem = new JMenuItem("Edit");
                JMenuItem deleteItem = new JMenuItem("Delete");
                
                editItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showEditUserDialog(id);
                    }
                });
                
                deleteItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteUser(id);
                    }
                });
                
                popupMenu.add(editItem);
                popupMenu.add(deleteItem);
                popupMenu.show(button, button.getWidth() / 2, button.getHeight() / 2);
            }
            isPushed = false;
            return "Edit/Delete";
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MenuManagementPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable menuTable;
    private JTextField searchField;
    private JTextField nameField;
    private JTextField priceField;
    private JComboBox<String> categoryComboBox;
    
    public MenuManagementPanel() {
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
        
        // Load menu items
        loadMenuItems();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = UITheme.createHeaderLabel("Menu Management");
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(15);
        JButton searchButton = UITheme.createButton("Search");
        JButton addButton = UITheme.createButton("Add Item");
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchMenuItems(searchField.getText());
            }
        });
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddItemDialog();
            }
        });
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        
        // Create table model with columns
        String[] columns = {"ID", "Name", "Category", "Price", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only actions column is editable
            }
        };
        
        // Create table
        menuTable = new JTable(tableModel);
        menuTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        menuTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        menuTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        menuTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        menuTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        // Add button renderer and editor to the actions column
        menuTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        menuTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(menuTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private void loadMenuItems() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Load menu items from file
        List<MenuItem> menuItems = FileHandler.loadMenuItems();
        
        // Add menu items to the table
        for (MenuItem item : menuItems) {
            Object[] row = {
                item.getId(),
                item.getName(),
                item.getCategory(),
                String.format("$%.2f", item.getPrice()),
                "Edit/Delete"
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchMenuItems(String searchTerm) {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Load menu items from file
        List<MenuItem> menuItems = FileHandler.loadMenuItems();
        
        // Filter and add menu items to the table
        for (MenuItem item : menuItems) {
            if (item.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                item.getCategory().toLowerCase().contains(searchTerm.toLowerCase())) {
                Object[] row = {
                    item.getId(),
                    item.getName(),
                    item.getCategory(),
                    String.format("$%.2f", item.getPrice()),
                    "Edit/Delete"
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void showAddItemDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Menu Item", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        
        JLabel priceLabel = new JLabel("Price:");
        priceField = new JTextField();
        
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Coffee", "Tea", "Pastry", "Sandwich", "Dessert"};
        categoryComboBox = new JComboBox<>(categories);
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        
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
                addMenuItem();
                dialog.dispose();
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void addMenuItem() {
        try {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            String category = (String) categoryComboBox.getSelectedItem();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name for the menu item.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            FileHandler.addMenuItem(name, price, category);
            loadMenuItems();
            
            JOptionPane.showMessageDialog(this, "Menu item added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showEditItemDialog(String id) {
        // Find the menu item
        List<MenuItem> menuItems = FileHandler.loadMenuItems();
        MenuItem itemToEdit = null;
        
        for (MenuItem item : menuItems) {
            if (item.getId().equals(id)) {
                itemToEdit = item;
                break;
            }
        }
        
        if (itemToEdit == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Menu Item", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(itemToEdit.getName());
        
        JLabel priceLabel = new JLabel("Price:");
        priceField = new JTextField(String.valueOf(itemToEdit.getPrice()));
        
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Coffee", "Tea", "Pastry", "Sandwich", "Dessert"};
        categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setSelectedItem(itemToEdit.getCategory());
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton updateButton = UITheme.createButton("Update");
        
        final MenuItem finalItemToEdit = itemToEdit;
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMenuItem(finalItemToEdit);
                dialog.dispose();
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(updateButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void updateMenuItem(MenuItem item) {
        try {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            String category = (String) categoryComboBox.getSelectedItem();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name for the menu item.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            item.setName(name);
            item.setPrice(price);
            item.setCategory(category);
            
            FileHandler.updateMenuItem(item);
            loadMenuItems();
            
            JOptionPane.showMessageDialog(this, "Menu item updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteMenuItem(String id) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this menu item?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            FileHandler.deleteMenuItem(id);
            loadMenuItems();
            JOptionPane.showMessageDialog(this, "Menu item deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
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
                        showEditItemDialog(id);
                    }
                });
                
                deleteItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteMenuItem(id);
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


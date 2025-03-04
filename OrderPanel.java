import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable orderTable;
    private JComboBox<String> statusFilterComboBox;
    private List<Order> orders;
    private List<MenuItem> menuItems;

    public OrderPanel() {
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

        // Load orders
        loadOrders();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = UITheme.createHeaderLabel("Order Management");

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel filterLabel = new JLabel("Filter by status:");
        String[] statuses = {"All Orders", "Pending", "Completed", "Cancelled"};
        statusFilterComboBox = new JComboBox<>(statuses);
        JButton newOrderButton = UITheme.createButton("New Order");

        statusFilterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterOrders();
            }
        });

        newOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNewOrderDialog();
            }
        });

        filterPanel.add(filterLabel);
        filterPanel.add(statusFilterComboBox);
        filterPanel.add(newOrderButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());

        // Create table model with columns
        String[] columns = {"Order ID", "Table", "Time", "Items", "Total", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only actions column is editable
            }
        };

        // Create table
        orderTable = new JTable(tableModel);
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Center align the table cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < orderTable.getColumnCount(); i++) {
            orderTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Add button renderer and editor to the actions column
        orderTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        orderTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Add custom renderer for status column
        orderTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(orderTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void loadOrders() {
        // Clear the table
        tableModel.setRowCount(0);

        // Load orders from file
        orders = FileHandler.loadOrders();
        menuItems = FileHandler.loadMenuItems();

        // Add orders to the table
        for (Order order : orders) {
            addOrderToTable(order);
        }
    }

    private void addOrderToTable(Order order) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Object[] row = {
                order.getId(),
                order.getTableNumber(),
                timeFormat.format(order.getTimestamp()),
                order.getItems().size() + " items",
                String.format("$%.2f", order.getTotal()),
                order.getStatus(),
                "View"
        };
        tableModel.addRow(row);
    }

    private void filterOrders() {
        // Clear the table
        tableModel.setRowCount(0);

        String filter = (String) statusFilterComboBox.getSelectedItem();

        // Add filtered orders to the table
        for (Order order : orders) {
            if (filter.equals("All Orders") ||
                    (filter.equals("Pending") && order.getStatus().equals("pending")) ||
                    (filter.equals("Completed") && order.getStatus().equals("completed")) ||
                    (filter.equals("Cancelled") && order.getStatus().equals("cancelled"))) {
                addOrderToTable(order);
            }
        }
    }

    private void showNewOrderDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create New Order", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table number panel
        JPanel tableNumberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel tableNumberLabel = new JLabel("Table Number:");
        JTextField tableNumberField = new JTextField(5);
        tableNumberPanel.add(tableNumberLabel);
        tableNumberPanel.add(tableNumberField);

        // Item selection panel
        JPanel itemSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel itemLabel = new JLabel("Menu Item:");
        JComboBox<String> itemComboBox = new JComboBox<>();

        // Populate item combo box
        for (MenuItem item : menuItems) {
            itemComboBox.addItem(item.getName() + " - $" + item.getPrice());
        }

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField("1", 3);
        JButton addItemButton = UITheme.createButton("Add");

        itemSelectionPanel.add(itemLabel);
        itemSelectionPanel.add(itemComboBox);
        itemSelectionPanel.add(quantityLabel);
        itemSelectionPanel.add(quantityField);
        itemSelectionPanel.add(addItemButton);

        // Order items panel
        JPanel orderItemsPanel = new JPanel(new BorderLayout());
        orderItemsPanel.setBorder(BorderFactory.createTitledBorder("Order Items"));

        String[] columns = {"Item", "Price", "Quantity", "Subtotal", "Actions"};
        DefaultTableModel orderItemsTableModel = new DefaultTableModel(columns, 0);
        JTable orderItemsTable = new JTable(orderItemsTableModel);

        JScrollPane orderItemsScrollPane = new JScrollPane(orderItemsTable);
        orderItemsPanel.add(orderItemsScrollPane, BorderLayout.CENTER);

        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(UITheme.HEADER_FONT);
        totalPanel.add(totalLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton createButton = UITheme.createButton("Create Order");

        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);

        // Add panels to main panel
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(tableNumberPanel);
        topPanel.add(itemSelectionPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(orderItemsPanel, BorderLayout.CENTER);
        mainPanel.add(totalPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // List to store order items
        List<Order.OrderItem> orderItems = new ArrayList<>();

        // Add item button action
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedIndex = itemComboBox.getSelectedIndex();
                    if (selectedIndex < 0) return;

                    MenuItem menuItem = menuItems.get(selectedIndex);
                    int quantity = Integer.parseInt(quantityField.getText());

                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Please enter a valid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Check if item already exists in order
                    boolean itemExists = false;
                    for (int i = 0; i < orderItemsTableModel.getRowCount(); i++) {
                        String itemName = (String) orderItemsTableModel.getValueAt(i, 0);
                        if (itemName.equals(menuItem.getName())) {
                            // Update quantity
                            int currentQuantity = (int) orderItemsTableModel.getValueAt(i, 2);
                            int newQuantity = currentQuantity + quantity;
                            double subtotal = menuItem.getPrice() * newQuantity;

                            orderItemsTableModel.setValueAt(newQuantity, i, 2);
                            orderItemsTableModel.setValueAt(String.format("$%.2f", subtotal), i, 3);

                            // Update order item
                            for (Order.OrderItem item : orderItems) {
                                if (item.getMenuItem().getId().equals(menuItem.getId())) {
                                    item.setQuantity(newQuantity);
                                    break;
                                }
                            }

                            itemExists = true;
                            break;
                        }
                    }

                    if (!itemExists) {
                        // Add new item to table
                        double subtotal = menuItem.getPrice() * quantity;
                        Object[] row = {
                                menuItem.getName(),
                                String.format("$%.2f", menuItem.getPrice()),
                                quantity,
                                String.format("$%.2f", subtotal),
                                "Remove"
                        };
                        orderItemsTableModel.addRow(row);

                        // Add to order items list
                        orderItems.add(new Order.OrderItem(menuItem, quantity));
                    }

                    // Update total
                    updateTotal(totalLabel, orderItems);

                    // Reset quantity
                    quantityField.setText("1");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Remove item action
        orderItemsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = orderItemsTable.rowAtPoint(evt.getPoint());
                int col = orderItemsTable.columnAtPoint(evt.getPoint());

                if (col == 4 && row >= 0) { // Actions column
                    // Remove from table
                    String itemName = (String) orderItemsTableModel.getValueAt(row, 0);
                    orderItemsTableModel.removeRow(row);

                    // Remove from order items list
                    for (int i = 0; i < orderItems.size(); i++) {
                        if (orderItems.get(i).getMenuItem().getName().equals(itemName)) {
                            orderItems.remove(i);
                            break;
                        }
                    }

                    // Update total
                    updateTotal(totalLabel, orderItems);
                }
            }
        });

        // Cancel button action
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        // Create order button action
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (orderItems.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Please add at least one item to the order.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String tableNumberText = tableNumberField.getText();
                    if (tableNumberText.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Please enter a table number.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int tableNumber = Integer.parseInt(tableNumberText);

                    // Create new order
                    Order newOrder = new Order(orderItems, tableNumber);

                    // Save order
                    FileHandler.saveOrder(newOrder);

                    // Refresh orders
                    loadOrders();

                    dialog.dispose();

                    JOptionPane.showMessageDialog(OrderPanel.this, "Order created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid table number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.setVisible(true);
    }

    private void updateTotal(JLabel totalLabel, List<Order.OrderItem> orderItems) {
        double total = 0;
        for (Order.OrderItem item : orderItems) {
            total += item.getMenuItem().getPrice() * item.getQuantity();
        }
        totalLabel.setText("Total: $" + String.format("%.2f", total));
    }

    private void showViewOrderDialog(String orderId) {
        // Find the order
        Order orderToView = null;
        for (Order order : orders) {
            if (order.getId().equals(orderId)) {
                orderToView = order;
                break;
            }
        }

        if (orderToView == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Order Details", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Order details panel
        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel orderIdLabel = new JLabel("Order ID: " + orderToView.getId());
        JLabel tableLabel = new JLabel("Table: " + orderToView.getTableNumber());
        JLabel timeLabel = new JLabel("Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderToView.getTimestamp()));
        JLabel statusLabel = new JLabel("Status: " + orderToView.getStatus());

        detailsPanel.add(orderIdLabel);
        detailsPanel.add(tableLabel);
        detailsPanel.add(timeLabel);
        detailsPanel.add(statusLabel);

        // Order items panel
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Order Items"));

        String[] columns = {"Item", "Price", "Quantity", "Subtotal"};
        DefaultTableModel itemsTableModel = new DefaultTableModel(columns, 0);
        JTable itemsTable = new JTable(itemsTableModel);

        // Add items to table
        for (Order.OrderItem item : orderToView.getItems()) {
            Object[] row = {
                    item.getMenuItem().getName(),
                    String.format("$%.2f", item.getMenuItem().getPrice()),
                    item.getQuantity(),
                    String.format("$%.2f", item.getSubtotal())
            };
            itemsTableModel.addRow(row);
        }

        JScrollPane itemsScrollPane = new JScrollPane(itemsTable);
        itemsPanel.add(itemsScrollPane, BorderLayout.CENTER);

        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Total: $" + String.format("%.2f", orderToView.getTotal()));
        totalLabel.setFont(UITheme.HEADER_FONT);
        totalPanel.add(totalLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");

        // Add action buttons based on order status
        final Order finalOrderToView = orderToView;

        if (orderToView.getStatus().equals("pending")) {
            JButton completeButton = UITheme.createButton("Complete Order");
            JButton cancelButton = UITheme.createButton("Cancel Order");

            completeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileHandler.updateOrderStatus(finalOrderToView.getId(), "completed");
                    loadOrders();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(OrderPanel.this, "Order marked as completed.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileHandler.updateOrderStatus(finalOrderToView.getId(), "cancelled");
                    loadOrders();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(OrderPanel.this, "Order cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            buttonPanel.add(cancelButton);
            buttonPanel.add(completeButton);
        } else if (orderToView.getStatus().equals("completed")) {
            JButton printButton = new JButton("Print Receipt");

            printButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    printReceipt(finalOrderToView);
                }
            });

            buttonPanel.add(printButton);
        }

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(closeButton);

        // Add panels to main panel
        mainPanel.add(detailsPanel, BorderLayout.NORTH);
        mainPanel.add(itemsPanel, BorderLayout.CENTER);
        mainPanel.add(totalPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void printReceipt(Order order) {
        FileHandler.saveReceipt(order);
        JOptionPane.showMessageDialog(this, "Receipt has been saved to file: receipt_table_" +
                        order.getTableNumber() + "_" + order.getId() + ".txt",
                "Receipt Printed", JOptionPane.INFORMATION_MESSAGE);
    }

    // Custom renderer for status column
    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String status = value.toString();
                if (status.equals("pending")) {
                    c.setForeground(new Color(255, 165, 0)); // Orange
                    setText("Pending");
                } else if (status.equals("completed")) {
                    c.setForeground(new Color(0, 128, 0)); // Green
                    setText("Completed");
                } else if (status.equals("cancelled")) {
                    c.setForeground(Color.RED);
                    setText("Cancelled");
                }
            }

            return c;
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
                showViewOrderDialog(id);
            }
            isPushed = false;
            return "View";
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}


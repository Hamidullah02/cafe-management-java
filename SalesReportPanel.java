import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SalesReportPanel extends JPanel {
    private JTextField startDateField;
    private JTextField endDateField;
    private JPanel metricsPanel;
    private JPanel chartPanel;
    private JTable topItemsTable;
    private JTable dailySalesTable;
    private DefaultTableModel topItemsTableModel;
    private DefaultTableModel dailySalesTableModel;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private List<Order> filteredOrders = new ArrayList<>();

    public SalesReportPanel() {
        // Set up the panel
        setLayout(new BorderLayout());
        setBorder(UITheme.PANEL_BORDER);

        // Create components
        JPanel headerPanel = createHeaderPanel();
        JPanel contentPanel = createContentPanel();

        // Add components to the main panel
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Initialize with default date range (last 7 days)
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date startDate = calendar.getTime();

        startDateField = new JTextField(10);
        endDateField = new JTextField(10);
        startDateField.setText(dateFormat.format(startDate));
        endDateField.setText(dateFormat.format(endDate));

        // Load initial report
        generateReport();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = UITheme.createHeaderLabel("Sales Report");

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JLabel startDateLabel = new JLabel("Start Date:");
        startDateField = new JTextField(10);

        JLabel endDateLabel = new JLabel("End Date:");
        endDateField = new JTextField(10);

        JButton generateButton = UITheme.createButton("Generate Report");
        JButton exportButton = UITheme.createButton("Export CSV");

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToCSV();
            }
        });

        controlsPanel.add(startDateLabel);
        controlsPanel.add(startDateField);
        controlsPanel.add(endDateLabel);
        controlsPanel.add(endDateField);
        controlsPanel.add(generateButton);
        controlsPanel.add(exportButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(controlsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());

        // Create metrics panel
        metricsPanel = createMetricsPanel();

        // Create chart panel
        chartPanel = createChartPanel();

        // Create tables panel
        JPanel tablesPanel = createTablesPanel();

        // Add components to content panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(metricsPanel, BorderLayout.NORTH);
        topPanel.add(chartPanel, BorderLayout.CENTER);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablesPanel, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createMetricsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Total Revenue Card
        JPanel revenueCard = createMetricCard("Total Revenue", "$0.00");

        // Total Orders Card
        JPanel ordersCard = createMetricCard("Total Orders", "0");

        // Average Order Value Card
        JPanel avgOrderCard = createMetricCard("Average Order Value", "$0.00");

        panel.add(revenueCard);
        panel.add(ordersCard);
        panel.add(avgOrderCard);

        return panel;
    }

    private JPanel createMetricCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(UITheme.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(UITheme.NORMAL_FONT);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(UITheme.TITLE_FONT);
        valueLabel.setName(title); // Use name to identify the label later

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Revenue by Day"));
        panel.setPreferredSize(new Dimension(800, 300));

        // Create a custom bar chart using Java2D
        JPanel barChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBarChart(g);
            }
        };

        panel.add(barChart, BorderLayout.CENTER);

        return panel;
    }

    private void drawBarChart(Graphics g) {
        if (filteredOrders.isEmpty()) {
            g.setColor(Color.GRAY);
            g.drawString("No data available for the selected date range", 50, 150);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 50;

        // Group orders by date
        Map<String, Double> revenueByDate = new TreeMap<>();
        for (Order order : filteredOrders) {
            String date = order.getFormattedDate();
            revenueByDate.put(date, revenueByDate.getOrDefault(date, 0.0) + order.getTotal());
        }

        // Find max revenue for scaling
        double maxRevenue = 0;
        for (Double revenue : revenueByDate.values()) {
            maxRevenue = Math.max(maxRevenue, revenue);
        }

        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
        g2d.drawLine(padding, padding, padding, height - padding); // Y-axis

        // Draw bars
        int barWidth = (width - 2 * padding) / Math.max(1, revenueByDate.size());
        int x = padding;

        for (Map.Entry<String, Double> entry : revenueByDate.entrySet()) {
            String date = entry.getKey();
            double revenue = entry.getValue();

            int barHeight = (int) ((revenue / maxRevenue) * (height - 2 * padding));

            g2d.setColor(UITheme.PRIMARY_COLOR);
            g2d.fillRect(x, height - padding - barHeight, barWidth - 5, barHeight);

            g2d.setColor(Color.BLACK);
            g2d.drawString(date, x, height - padding + 15);
            g2d.drawString(String.format("$%.2f", revenue), x, height - padding - barHeight - 5);

            x += barWidth;
        }

        // Draw Y-axis labels
        g2d.drawString("$0", padding - 30, height - padding);
        g2d.drawString(String.format("$%.2f", maxRevenue), padding - 30, padding);
    }

    private JPanel createTablesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Top selling items table
        JPanel topItemsPanel = new JPanel(new BorderLayout());
        topItemsPanel.setBorder(BorderFactory.createTitledBorder("Top Selling Items"));

        String[] topItemsColumns = {"Item", "Quantity Sold"};
        topItemsTableModel = new DefaultTableModel(topItemsColumns, 0);
        topItemsTable = new JTable(topItemsTableModel);

        JScrollPane topItemsScrollPane = new JScrollPane(topItemsTable);
        topItemsPanel.add(topItemsScrollPane, BorderLayout.CENTER);

        // Daily sales table
        JPanel dailySalesPanel = new JPanel(new BorderLayout());
        dailySalesPanel.setBorder(BorderFactory.createTitledBorder("Daily Sales"));

        String[] dailySalesColumns = {"Date", "Orders", "Revenue"};
        dailySalesTableModel = new DefaultTableModel(dailySalesColumns, 0);
        dailySalesTable = new JTable(dailySalesTableModel);

        JScrollPane dailySalesScrollPane = new JScrollPane(dailySalesTable);
        dailySalesPanel.add(dailySalesScrollPane, BorderLayout.CENTER);

        panel.add(topItemsPanel);
        panel.add(dailySalesPanel);

        return panel;
    }

    private void generateReport() {
        try {
            // Parse date range
            Date startDate = dateFormat.parse(startDateField.getText());
            Date endDate = dateFormat.parse(endDateField.getText());

            // Add one day to end date to include the end date in the range
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            endDate = calendar.getTime();

            // Get orders in date range
            filteredOrders = FileHandler.getOrdersByDateRange(startDate, endDate);

            // Update metrics
            updateMetrics();

            // Update tables
            updateTables();

            // Repaint chart
            chartPanel.repaint();

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid dates in the format YYYY-MM-DD",
                    "Invalid Date Format",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMetrics() {
        double totalRevenue = 0;
        int totalOrders = filteredOrders.size();

        for (Order order : filteredOrders) {
            if (order.getStatus().equals("completed")) {
                totalRevenue += order.getTotal();
            }
        }

        double avgOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        // Update metric cards
        for (Component component : metricsPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel card = (JPanel) component;
                for (Component cardComponent : card.getComponents()) {
                    if (cardComponent instanceof JLabel) {
                        JLabel label = (JLabel) cardComponent;
                        if (label.getName() != null) {
                            if (label.getName().equals("Total Revenue")) {
                                label.setText(String.format("$%.2f", totalRevenue));
                            } else if (label.getName().equals("Total Orders")) {
                                label.setText(String.valueOf(totalOrders));
                            } else if (label.getName().equals("Average Order Value")) {
                                label.setText(String.format("$%.2f", avgOrderValue));
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateTables() {
        // Clear tables
        topItemsTableModel.setRowCount(0);
        dailySalesTableModel.setRowCount(0);

        if (filteredOrders.isEmpty()) {
            return;
        }

        // Calculate top selling items
        Map<String, Integer> itemCounts = new HashMap<>();
        for (Order order : filteredOrders) {
            for (Order.OrderItem item : order.getItems()) {
                String itemName = item.getMenuItem().getName();
                itemCounts.put(itemName, itemCounts.getOrDefault(itemName, 0) + item.getQuantity());
            }
        }

        // Sort items by quantity sold
        List<Map.Entry<String, Integer>> sortedItems = new ArrayList<>(itemCounts.entrySet());
        sortedItems.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Add top 5 items to table
        for (int i = 0; i < Math.min(5, sortedItems.size()); i++) {
            Map.Entry<String, Integer> entry = sortedItems.get(i);
            Object[] row = {entry.getKey(), entry.getValue()};
            topItemsTableModel.addRow(row);
        }

        // Calculate daily sales
        Map<String, DailySales> salesByDate = new TreeMap<>();
        for (Order order : filteredOrders) {
            String date = order.getFormattedDate();
            DailySales dailySales = salesByDate.getOrDefault(date, new DailySales());
            dailySales.orders++;
            dailySales.revenue += order.getTotal();
            salesByDate.put(date, dailySales);
        }

        // Add daily sales to table
        for (Map.Entry<String, DailySales> entry : salesByDate.entrySet()) {
            Object[] row = {
                    entry.getKey(),
                    entry.getValue().orders,
                    String.format("$%.2f", entry.getValue().revenue)
            };
            dailySalesTableModel.addRow(row);
        }
    }

    private void exportToCSV() {
        if (filteredOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No data to export",
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String fileName = "sales_report_" + startDateField.getText() + "_to_" + endDateField.getText() + ".csv";

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            // Write header
            writer.write("Date,Orders,Revenue");
            writer.newLine();

            // Group orders by date
            Map<String, DailySales> salesByDate = new TreeMap<>();
            for (Order order : filteredOrders) {
                String date = order.getFormattedDate();
                DailySales dailySales = salesByDate.getOrDefault(date, new DailySales());
                dailySales.orders++;
                dailySales.revenue += order.getTotal();
                salesByDate.put(date, dailySales);
            }

            // Write data
            for (Map.Entry<String, DailySales> entry : salesByDate.entrySet()) {
                writer.write(entry.getKey() + "," +
                        entry.getValue().orders + "," +
                        String.format("%.2f", entry.getValue().revenue));
                writer.newLine();
            }

            writer.close();

            JOptionPane.showMessageDialog(this,
                    "Report exported to " + fileName,
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper class for daily sales calculations
    private class DailySales {
        int orders = 0;
        double revenue = 0;
    }
}


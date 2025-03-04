import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FileHandler {
    private static final String USERS_FILE = "users.txt";
    private static final String MENU_FILE = "menu.txt";
    private static final String ORDERS_DIRECTORY = "orders";
    private static final String SALES_REPORTS_DIRECTORY = "sales_reports";
    
    static {
        new File(ORDERS_DIRECTORY).mkdirs();
        new File(SALES_REPORTS_DIRECTORY).mkdirs();
        
        if (!new File(USERS_FILE).exists()) {
            List<User> defaultUsers = new ArrayList<>();
            defaultUsers.add(new User("1", "admin", "admin", true));
            defaultUsers.add(new User("2", "staff", "staff", false));
            saveUsers(defaultUsers);
        }
        
        if (!new File(MENU_FILE).exists()) {
            List<MenuItem> defaultItems = new ArrayList<>();
            defaultItems.add(new MenuItem("1", "Espresso", 2.5, "Coffee"));
            defaultItems.add(new MenuItem("2", "Cappuccino", 3.5, "Coffee"));
            defaultItems.add(new MenuItem("3", "Croissant", 2.0, "Pastry"));
            saveMenuItems(defaultItems);
        }
    }
    
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String id = parts[0];
                    String username = parts[1];
                    String password = parts[2];
                    boolean isAdmin = Boolean.parseBoolean(parts[3]);
                    users.add(new User(id, username, password, isAdmin));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public static void saveUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                writer.write(user.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static User addUser(String username, String password, boolean isAdmin) {
        List<User> users = loadUsers();
        String id = UUID.randomUUID().toString().substring(0, 8);
        User newUser = new User(id, username, password, isAdmin);
        users.add(newUser);
        saveUsers(users);
        return newUser;
    }
    
    public static void updateUser(User user) {
        List<User> users = loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                break;
            }
        }
        saveUsers(users);
    }
    
    public static void deleteUser(String id) {
        List<User> users = loadUsers();
        users.removeIf(user -> user.getId().equals(id));
        saveUsers(users);
    }
    
    // Menu operations
    public static List<MenuItem> loadMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(MENU_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String id = parts[0];
                    String name = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    String category = parts[3];
                    menuItems.add(new MenuItem(id, name, price, category));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return menuItems;
    }
    
    public static void saveMenuItems(List<MenuItem> menuItems) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MENU_FILE))) {
            for (MenuItem item : menuItems) {
                writer.write(item.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static MenuItem addMenuItem(String name, double price, String category) {
        List<MenuItem> menuItems = loadMenuItems();
        String id = UUID.randomUUID().toString().substring(0, 8);
        MenuItem newItem = new MenuItem(id, name, price, category);
        menuItems.add(newItem);
        saveMenuItems(menuItems);
        return newItem;
    }
    
    public static void updateMenuItem(MenuItem item) {
        List<MenuItem> menuItems = loadMenuItems();
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getId().equals(item.getId())) {
                menuItems.set(i, item);
                break;
            }
        }
        saveMenuItems(menuItems);
    }
    
    public static void deleteMenuItem(String id) {
        List<MenuItem> menuItems = loadMenuItems();
        menuItems.removeIf(item -> item.getId().equals(id));
        saveMenuItems(menuItems);
    }
    
    // Order operations
    public static List<Order> loadOrders() {
        List<Order> orders = new ArrayList<>();
        File ordersDir = new File(ORDERS_DIRECTORY);
        File[] orderFiles = ordersDir.listFiles((dir, name) -> name.endsWith(".txt"));
        
        if (orderFiles != null) {
            for (File file : orderFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String headerLine = reader.readLine();
                    if (headerLine == null) continue;
                    
                    String[] headerParts = headerLine.split(",");
                    if (headerParts.length < 5) continue;
                    
                    String id = headerParts[0];
                    int tableNumber = Integer.parseInt(headerParts[1]);
                    Date timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(headerParts[2]);
                    String status = headerParts[3];
                    
                    List<Order.OrderItem> items = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        
                        String[] parts = line.split(",");
                        if (parts.length >= 2) {
                            String menuItemId = parts[0];
                            int quantity = Integer.parseInt(parts[1]);
                            
                            // Find the menu item
                            MenuItem menuItem = null;
                            for (MenuItem item : loadMenuItems()) {
                                if (item.getId().equals(menuItemId)) {
                                    menuItem = item;
                                    break;
                                }
                            }
                            
                            if (menuItem != null) {
                                items.add(new Order.OrderItem(menuItem, quantity));
                            }
                        }
                    }
                    
                    orders.add(new Order(id, items, tableNumber, timestamp, status));
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return orders;
    }
    
    public static void saveOrder(Order order) {
        String fileName = ORDERS_DIRECTORY + File.separator + 
                          new SimpleDateFormat("yyyy-MM-dd").format(order.getTimestamp()) + 
                          "_order_" + order.getId() + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(order.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void updateOrderStatus(String orderId, String status) {
        List<Order> orders = loadOrders();
        for (Order order : orders) {
            if (order.getId().equals(orderId)) {
                order.setStatus(status);
                saveOrder(order);
                break;
            }
        }
    }
    
    public static void deleteOrder(String orderId) {
        File ordersDir = new File(ORDERS_DIRECTORY);
        File[] orderFiles = ordersDir.listFiles((dir, name) -> name.contains("_order_" + orderId));
        
        if (orderFiles != null) {
            for (File file : orderFiles) {
                file.delete();
            }
        }
    }
    
    // Sales report operations
    public static List<Order> getOrdersByDateRange(Date startDate, Date endDate) {
        List<Order> allOrders = loadOrders();
        List<Order> filteredOrders = new ArrayList<>();
        
        for (Order order : allOrders) {
            if (order.getTimestamp().compareTo(startDate) >= 0 && 
                order.getTimestamp().compareTo(endDate) <= 0) {
                filteredOrders.add(order);
            }
        }
        
        return filteredOrders;
    }
    
    public static void saveReceipt(Order order) {
        String fileName = "receipt_table_" + order.getTableNumber() + "_" + order.getId() + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("===== CAFE RECEIPT =====");
            writer.newLine();
            writer.write("Table: " + order.getTableNumber());
            writer.newLine();
            writer.write("Date: " + order.getFormattedTimestamp());
            writer.newLine();
            writer.write("Order ID: " + order.getId());
            writer.newLine();
            writer.write("=======================");
            writer.newLine();
            
            for (Order.OrderItem item : order.getItems()) {
                writer.write(item.getQuantity() + " x " + item.getMenuItem().getName() + 
                             " - $" + String.format("%.2f", item.getSubtotal()));
                writer.newLine();
            }
            
            writer.write("=======================");
            writer.newLine();
            writer.write("Total: $" + String.format("%.2f", order.getTotal()));
            writer.newLine();
            writer.write("Thank you for your visit!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


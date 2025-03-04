import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Order {
    private String id;
    private List<OrderItem> items;
    private int tableNumber;
    private Date timestamp;
    private String status;
    private double total;
    
    public Order(String id, List<OrderItem> items, int tableNumber, Date timestamp, String status) {
        this.id = id;
        this.items = items;
        this.tableNumber = tableNumber;
        this.timestamp = timestamp;
        this.status = status;
        calculateTotal();
    }
    
    public Order(List<OrderItem> items, int tableNumber) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.items = items;
        this.tableNumber = tableNumber;
        this.timestamp = new Date();
        this.status = "pending";
        calculateTotal();
    }
    
    private void calculateTotal() {
        this.total = 0;
        for (OrderItem item : items) {
            this.total += item.getMenuItem().getPrice() * item.getQuantity();
        }
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotal();
    }
    
    public int getTableNumber() {
        return tableNumber;
    }
    
    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public double getTotal() {
        return total;
    }
    
    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }
    
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(timestamp);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",");
        sb.append(tableNumber).append(",");
        sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp)).append(",");
        sb.append(status).append(",");
        sb.append(total).append("\n");
        
        for (OrderItem item : items) {
            sb.append(item.toString()).append("\n");
        }
        
        return sb.toString();
    }
    
    public static class OrderItem {
        private MenuItem menuItem;
        private int quantity;
        
        public OrderItem(MenuItem menuItem, int quantity) {
            this.menuItem = menuItem;
            this.quantity = quantity;
        }
        
        // Getters and setters
        public MenuItem getMenuItem() {
            return menuItem;
        }
        
        public void setMenuItem(MenuItem menuItem) {
            this.menuItem = menuItem;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        
        public double getSubtotal() {
            return menuItem.getPrice() * quantity;
        }
        
        @Override
        public String toString() {
            return menuItem.getId() + "," + quantity;
        }
    }
}


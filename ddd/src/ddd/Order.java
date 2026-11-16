package ddd;

public class Order {

    private long id;
    private String customerName;
    private String productName;
    private int price;
    private int quantity;
    private String address;
    private int totalPrice;
    private String status;

    public Order(long id, String customerName, String productName, int price, int quantity, String address) {
        this.id = id;
        this.customerName = customerName;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.address = address;
        this.totalPrice = price * quantity;
        this.status = "주문 접수";
    }

    public long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductName() {
        return productName;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getAddress() {
        return address;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Order other)) {
            return false;
        }
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}

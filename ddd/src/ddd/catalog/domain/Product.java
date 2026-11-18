package ddd.catalog.domain;

public class Product {

    private final long id;
    private final String name;
    private final int price;
    private int stock;

    public Product(long id, String name, int price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void decreaseStock(int quantity) {
        stock = stock - quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Product other)) {
            return false;
        }
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}

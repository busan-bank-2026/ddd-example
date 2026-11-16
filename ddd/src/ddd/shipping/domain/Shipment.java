package ddd.shipping.domain;

public class Shipment {

    private final long orderId;
    private final String address;

    public Shipment(long orderId, String address) {
        this.orderId = orderId;
        this.address = address;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Shipment other)) {
            return false;
        }
        return orderId == other.orderId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(orderId);
    }
}

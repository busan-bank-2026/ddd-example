package ddd.order.domain;

import java.util.ArrayList;
import java.util.List;


public class Order {

    private long id;
    private long memberId;
    private List<OrderItem> items;
    private Address address;
    private int totalPrice;
    private int discountPrice;
    private OrderStatus status;
    private List<Object> events;
    private long version;

    public Order(long id, long memberId, List<OrderItem> items, Address address) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("주문 항목이 하나 이상 있어야 합니다");
        }
        this.id = id;
        this.memberId = memberId;
        this.items = new ArrayList<>(items);
        this.address = address;
        this.totalPrice = 0;
        for (OrderItem item : items) {
            totalPrice = totalPrice + item.getAmount();
        }
        this.discountPrice = 0;
        this.status = OrderStatus.ORDERED;
        this.events = new ArrayList<>();
        events.add(new OrderPlaced(id, List.copyOf(items)));
    }

    public Order(Order other) {
        this.id = other.id;
        this.memberId = other.memberId;
        this.items = new ArrayList<>(other.items);
        this.address = other.address;
        this.totalPrice = other.totalPrice;
        this.discountPrice = other.discountPrice;
        this.status = other.status;
        this.events = new ArrayList<>(other.events);
        this.version = other.version;
    }

    public long getId() {
        return id;
    }

    public long getMemberId() {
        return memberId;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public Address getAddress() {
        return address;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public int getPaymentPrice() {
        return totalPrice - discountPrice;
    }

    public int getShippingFee() {
        if (new FreeShippingSpecification().isSatisfiedBy(this)) {
            return 0;
        }
        return 3000;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<Object> getEvents() {
        return List.copyOf(events);
    }

    public long getVersion() {
        return version;
    }

    public void increaseVersion() {
        version++;
    }

    public void addItem(OrderItem item) {
        if (status != OrderStatus.ORDERED) {
            throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
        }
        items.add(item);
        totalPrice = totalPrice + item.getAmount();
    }

    public void pay(int discountPrice) {
        if (status != OrderStatus.ORDERED) {
            throw new IllegalStateException(status.label() + "인 주문은 결제할 수 없습니다");
        }
        this.discountPrice = discountPrice;
        status = OrderStatus.PAID;
    }

    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException(status.label() + "인 주문은 배송할 수 없습니다");
        }
        status = OrderStatus.SHIPPING;
    }

    public void deliver() {
        if (status != OrderStatus.SHIPPING) {
            throw new IllegalStateException(status.label() + "인 주문은 배송 완료할 수 없습니다");
        }
        status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (status != OrderStatus.ORDERED && status != OrderStatus.PAID) {
            throw new IllegalStateException(status.label() + "인 주문은 취소할 수 없습니다");
        }
        status = OrderStatus.CANCELED;
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

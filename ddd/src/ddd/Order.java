package ddd;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private long id;
    private String customerName;
    private List<OrderLine> orderLines;
    private Address address;
    private Money totalPrice;
    private OrderStatus status;

    public Order(long id, String customerName, List<OrderLine> orderLines, Address address) {
        if (orderLines.isEmpty()) {
            throw new IllegalArgumentException("주문 항목이 하나 이상 있어야 합니다");
        }
        this.id = id;
        this.customerName = customerName;
        this.orderLines = new ArrayList<>(orderLines);
        this.address = address;
        this.totalPrice = new Money(0);
        for (OrderLine orderLine : orderLines) {
            totalPrice = totalPrice.plus(orderLine.amount());
        }
        this.status = OrderStatus.ORDERED;
    }

    public long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<OrderLine> getOrderLines() {
        return List.copyOf(orderLines);
    }

    public Address getAddress() {
        return address;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void addOrderLine(OrderLine orderLine) {
        if (status != OrderStatus.ORDERED) {
            throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
        }
        orderLines.add(orderLine);
        totalPrice = totalPrice.plus(orderLine.amount());
    }

    public void pay() {
        status = OrderStatus.PAID;
    }

    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("결제하지 않은 주문은 배송할 수 없습니다");
        }
        status = OrderStatus.SHIPPING;
    }

    public void cancel() {
        if (status == OrderStatus.SHIPPING) {
            throw new IllegalStateException("배송 중인 주문은 취소할 수 없습니다");
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

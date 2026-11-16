package ddd.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private long memberId;
    private List<OrderLine> orderLines;
    private Address address;
    private Money totalPrice;
    private Money discountPrice;
    private OrderStatus status;
    private List<Object> events;

    public Order(long id, long memberId, List<OrderLine> orderLines, Address address) {
        if (orderLines.isEmpty()) {
            throw new IllegalArgumentException("주문 항목이 하나 이상 있어야 합니다");
        }
        this.id = id;
        this.memberId = memberId;
        this.orderLines = new ArrayList<>(orderLines);
        this.address = address;
        this.totalPrice = new Money(0);
        for (OrderLine orderLine : orderLines) {
            totalPrice = totalPrice.plus(orderLine.amount());
        }
        this.discountPrice = new Money(0);
        this.status = OrderStatus.ORDERED;
        this.events = new ArrayList<>();
        events.add(new OrderPlaced(id, List.copyOf(orderLines)));
    }

    public long getId() {
        return id;
    }

    public long getMemberId() {
        return memberId;
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

    public Money getDiscountPrice() {
        return discountPrice;
    }

    public Money getPaymentPrice() {
        return totalPrice.minus(discountPrice);
    }

    public Money getShippingFee() {
        if (new FreeShippingSpecification().isSatisfiedBy(this)) {
            return new Money(0);
        }
        return new Money(3000);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<Object> getEvents() {
        return List.copyOf(events);
    }

    public void addOrderLine(OrderLine orderLine) {
        if (status != OrderStatus.ORDERED) {
            throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
        }
        orderLines.add(orderLine);
        totalPrice = totalPrice.plus(orderLine.amount());
    }

    public void pay(Money discountPrice) {
        if (!status.canChangeTo(OrderStatus.PAID)) {
            throw new IllegalStateException(status.label() + "인 주문은 결제할 수 없습니다");
        }
        this.discountPrice = discountPrice;
        status = OrderStatus.PAID;
    }

    public void ship() {
        if (!status.canChangeTo(OrderStatus.SHIPPING)) {
            throw new IllegalStateException(status.label() + "인 주문은 배송할 수 없습니다");
        }
        status = OrderStatus.SHIPPING;
    }

    public void deliver() {
        if (!status.canChangeTo(OrderStatus.DELIVERED)) {
            throw new IllegalStateException(status.label() + "인 주문은 배송 완료할 수 없습니다");
        }
        status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (!status.canChangeTo(OrderStatus.CANCELED)) {
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

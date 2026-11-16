package ddd;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private long id;
    private String customerName;
    private List<OrderLine> orderLines;
    private Address address;
    private Money totalPrice;
    private String status;

    public Order(long id, String customerName, List<OrderLine> orderLines, Address address) {
        this.id = id;
        this.customerName = customerName;
        this.orderLines = new ArrayList<>(orderLines);
        this.address = address;
        this.totalPrice = new Money(0);
        for (OrderLine orderLine : orderLines) {
            totalPrice = totalPrice.plus(orderLine.amount());
        }
        this.status = "주문 접수";
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

    public String getStatus() {
        return status;
    }

    public void addOrderLine(OrderLine orderLine) {
        if (!status.equals("주문 접수")) {
            throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
        }
        orderLines.add(orderLine);
        totalPrice = totalPrice.plus(orderLine.amount());
    }

    public void pay() {
        status = "결제 완료";
    }

    public void ship() {
        if (!status.equals("결제 완료")) {
            throw new IllegalStateException("결제하지 않은 주문은 배송할 수 없습니다");
        }
        status = "배송 중";
    }

    public void cancel() {
        if (status.equals("배송 중")) {
            throw new IllegalStateException("배송 중인 주문은 취소할 수 없습니다");
        }
        status = "취소";
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

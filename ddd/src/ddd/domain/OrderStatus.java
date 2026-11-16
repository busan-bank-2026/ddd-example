package ddd.domain;

public enum OrderStatus {
    ORDERED("주문 접수"),
    PAID("결제 완료"),
    SHIPPING("배송 중"),
    DELIVERED("배송 완료"),
    CANCELED("취소");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public boolean canChangeTo(OrderStatus next) {
        return switch (this) {
            case ORDERED -> next == PAID || next == CANCELED;
            case PAID -> next == SHIPPING || next == CANCELED;
            case SHIPPING -> next == DELIVERED;
            case DELIVERED, CANCELED -> false;
        };
    }
}

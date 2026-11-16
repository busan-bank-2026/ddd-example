package ddd.order.domain;

public record PaymentResult(boolean approved, String reason) {
}

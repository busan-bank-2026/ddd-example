package ddd.order.application;

import ddd.order.domain.Order;

public class OrderPaidNotifier {

    private final MessageSender messageSender;

    public OrderPaidNotifier(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void notifyPaid(Order order) {
        String message = "ORDER_PAID|orderId=" + order.getId() + "|address=" + order.getAddress().value();
        messageSender.send(message);
    }
}

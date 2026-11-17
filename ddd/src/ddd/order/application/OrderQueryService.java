package ddd.order.application;

import java.util.ArrayList;
import java.util.List;

import ddd.member.domain.Member;
import ddd.member.domain.MemberRepository;
import ddd.order.domain.Order;
import ddd.order.domain.OrderRepository;

public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;

    public OrderQueryService(OrderRepository orderRepository, MemberRepository memberRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
    }

    public List<OrderSummary> findOrderSummaries(long memberId) {
        List<OrderSummary> summaries = new ArrayList<>();
        for (Order order : orderRepository.findByMemberId(memberId)) {
            Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
            summaries.add(new OrderSummary(order.getId(), member.getName(), order.getItems().size(),
                    order.getPaymentPrice(), order.getStatus().label()));
        }
        return summaries;
    }
}

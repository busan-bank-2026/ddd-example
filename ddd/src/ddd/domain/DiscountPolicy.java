package ddd.domain;

public class DiscountPolicy {

    public Money discount(Member member, Order order) {
        if (member.getGrade() == MemberGrade.VIP) {
            return new Money(order.getTotalPrice().amount() * 10 / 100);
        }
        return new Money(0);
    }
}

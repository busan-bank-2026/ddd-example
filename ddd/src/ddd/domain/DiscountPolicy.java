package ddd.domain;

import ddd.member.domain.Member;
import ddd.member.domain.MemberGrade;
import ddd.shared.Money;

public class DiscountPolicy {

    public Money discount(Member member, Order order) {
        if (member.getGrade() == MemberGrade.VIP) {
            return new Money(order.getTotalPrice().amount() * 10 / 100);
        }
        return new Money(0);
    }
}

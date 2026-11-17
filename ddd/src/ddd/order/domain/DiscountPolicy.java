package ddd.order.domain;

import ddd.member.domain.Member;
import ddd.member.domain.MemberGrade;

public class DiscountPolicy {

    public int discount(Member member, Order order) {
        if (member.getGrade() == MemberGrade.VIP) {
            return order.getTotalPrice() * 10 / 100;
        }
        return 0;
    }
}

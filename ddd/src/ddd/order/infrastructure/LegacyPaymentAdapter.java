package ddd.order.infrastructure;

import ddd.order.domain.PaymentGateway;
import ddd.order.domain.PaymentResult;
import ddd.shared.Money;
import legacypay.LegacyPayGateway;

public class LegacyPaymentAdapter implements PaymentGateway {

    private static final String MERCHANT_KEY = "DDD-SHOP";

    private final LegacyPayGateway legacyPayGateway;

    public LegacyPaymentAdapter(LegacyPayGateway legacyPayGateway) {
        this.legacyPayGateway = legacyPayGateway;
    }

    @Override
    public PaymentResult pay(Money amount) {
        String code = legacyPayGateway.approve(MERCHANT_KEY, amount.amount());
        if (code.equals("0000")) {
            return new PaymentResult(true, "");
        }
        if (code.equals("E101")) {
            return new PaymentResult(false, "결제 한도를 넘었습니다");
        }
        return new PaymentResult(false, "결제에 실패했습니다");
    }
}

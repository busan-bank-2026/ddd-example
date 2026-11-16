package legacypay;

public class LegacyPayGateway {

    public String approve(String merchantKey, int amountWon) {
        if (!merchantKey.equals("DDD-SHOP")) {
            return "E201";
        }
        if (amountWon > 100000) {
            return "E101";
        }
        return "0000";
    }
}

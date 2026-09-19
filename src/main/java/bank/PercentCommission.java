package bank;

public class PercentCommission implements CommissionPolicy {
    private final double percent;

    public PercentCommission(double percent) {
        this.percent = percent;
    }

    @Override
    public double calculate(double amount) {
        return amount * (percent / 100.0);
    }
}
package bank;

import bank.domain.CommissionPolicy;

/*
07.11 этап 3: OCP (новая стратегия без изменения старого кода).
Фиксированная комиссия.
 */
public class FixedCommissionPolicy implements CommissionPolicy {
    private final double fixedAmount;

    public FixedCommissionPolicy(double fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    @Override
    public double calculate(double amount) {
        return fixedAmount;
    }
}
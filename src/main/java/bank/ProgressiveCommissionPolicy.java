package bank;

import bank.domain.CommissionPolicy;

/*
07.11 этап 3: OCP.
Прогрессивная комиссия (чем больше сумма, тем больше процент).
 */
public class ProgressiveCommissionPolicy implements CommissionPolicy {
    @Override
    public double calculate(double amount) {
        if (amount < 1000) {
            return 0; // до 1000 без комиссии
        } else if (amount <= 50_000) {
            return amount * 0.01; // 1%
        } else {
            return amount * 0.02; // 2%
        }
    }
}
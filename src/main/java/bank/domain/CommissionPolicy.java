package bank.domain;

@FunctionalInterface
public interface CommissionPolicy {
    double calculate(double amount);
}


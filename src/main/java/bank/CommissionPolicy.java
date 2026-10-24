package bank;

@FunctionalInterface
public interface CommissionPolicy {
    double calculate(double amount);
}


package bank;

public class SavingsAccount extends BankAccount {
    private final double minimumBalance;

    public SavingsAccount(String number, String owner, double initialBalance, double minimumBalance) {
        super(number, owner, initialBalance);
        this.minimumBalance = minimumBalance;
    }
    /*
    У него дополнительно задаётся минимальный остаток:
    private final double minimumBalance;
    После снятия денег баланс не должен становиться меньше minimumBalance.
    Пример:
    balance = 10 000
    minimumBalance = 1 000

    withdraw(8 500)
        true
        balance = 1 500

    withdraw(1 000)
        false
        balance = 1 500
     */
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (getBalance() - amount < minimumBalance) {
            return false;
        }
        setBalance(getBalance() - amount);
        return true;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }
}
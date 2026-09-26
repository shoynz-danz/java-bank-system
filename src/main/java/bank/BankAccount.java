package bank;
import java.util.Objects;

public abstract class BankAccount {
    private final String number;
    private final String owner;
    private double balance;

    /*
    1. Номер счёта после создания изменяться не должен.
    2. Владелец счёта после создания изменяться не должен.
    3. Баланс нельзя изменять напрямую из других классов.
    4. Пополнение на нулевую или отрицательную сумму не должно изменять баланс.
    5. Начальный баланс не должен быть отрицательным.
     */

    protected BankAccount(String number, String owner, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Баланс отрицательный!!!");
        }
        this.number = number;
        this.owner = owner;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public abstract boolean withdraw(double amount);

    public double getBalance() {
        return balance;
    }

    public String getNumber() {
        return number;
    }

    public String getOwner() {
        return owner;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{ number='" + number + "', owner='" + owner + "', balance=" + balance + " }";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BankAccount)) return false;
        BankAccount other = (BankAccount) obj;
        return Objects.equals(number, other.number);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}
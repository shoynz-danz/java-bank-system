package bank;

import java.util.Objects;

public abstract class BankAccount {
    private final AccountNumber number;
    private final String owner;
    private double balance;

    protected BankAccount(AccountNumber number, String owner, double initialBalance) {
        if (number == null) {
            throw new IllegalArgumentException("Номер счета не может быть null");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Баланс отрицательный!!!");
        }
        this.number = number;
        this.owner = owner;
        this.balance = initialBalance;
    }

    protected BankAccount(String number, String owner, double initialBalance) {
        this(new AccountNumber(number), owner, initialBalance);
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        increaseBalance(amount);
    }

    private void increaseBalance(double amount) {
        this.balance += amount;
    }

    public abstract boolean withdraw(double amount);

    public double getBalance() {
        return balance;
    }

    public AccountNumber getNumber() {
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
        return getClass().getSimpleName() + "{ number='" + number.value() + "', owner='" + owner + "', balance=" + balance + " }";
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
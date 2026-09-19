package bank;

public class CreditAccount extends BankAccount {
    private final double creditLimit;

    public CreditAccount(String number, String owner, double initialBalance, double creditLimit) {
        super(number, owner, initialBalance);
        this.creditLimit = creditLimit;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (getBalance() - amount < -creditLimit) {
            return false;
        }
        setBalance(getBalance() - amount);
        return true;
    }

    public double getCreditLimit() {
        return creditLimit;
    }
}

/*
есть кредитный лимит:
private final double creditLimit;
Такой счёт может иметь отрицательный баланс, но не ниже значения:
-creditLimit
Пример:
balance = 1 000
creditLimit = 5 000

withdraw(4 000)
  true
  balance = -3 000

withdraw(3 000)
  false
  balance остаётся -3 000
Продумать, какие методы базового класса должны быть доступны наследникам, но не внешнему коду.
После завершения этапа сделать commit.
 */
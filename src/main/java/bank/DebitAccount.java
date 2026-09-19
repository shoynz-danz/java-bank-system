package bank;

public class DebitAccount extends BankAccount {

    public DebitAccount(String number, String owner, double initialBalance) {
        super(number, owner, initialBalance);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > getBalance()) {
            return false;
        }
        setBalance(getBalance() - amount);
        return true;
    }
}

/*
    * счёт нельзя увести в отрицательный баланс;
    * если денег недостаточно, withdraw() возвращает false;
    * баланс при этом не изменяется;
    * при успешном снятии withdraw() возвращает true
    * Пример:
        balance = 10 000

        withdraw(8 000)
         true
         balance = 2 000

        withdraw(3 000)
          false
          balance = 2 000
     */
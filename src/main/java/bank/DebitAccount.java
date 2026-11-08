package bank;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;

public class DebitAccount extends BankAccount {

    public DebitAccount(AccountNumber number, String owner, double initialBalance) {
        super(number, owner, initialBalance);
    }

    public DebitAccount(String number, String owner, double initialBalance) {
        super(number, owner, initialBalance);
    }

    @Override
    protected double getAvailableAmount() {
        return getBalance();
    }
}
package bank;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;

public interface AccountFactory {
    BankAccount create(
            AccountType type,
            AccountNumber number,
            String owner,
            double initialBalance);
}
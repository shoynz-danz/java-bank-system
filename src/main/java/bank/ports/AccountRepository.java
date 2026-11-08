package bank.ports;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;

import java.util.Optional;

public interface AccountRepository {
    Optional<BankAccount> findById(AccountNumber number);
    void save(BankAccount account);
}
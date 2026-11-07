package bank;

import java.util.Optional;

public interface AccountRepository {
    Optional<BankAccount> findById(AccountNumber number);
    void save(BankAccount account);
}
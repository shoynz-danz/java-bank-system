package bank;

import java.util.Optional;

/*
07.11 этап 2: сервис отвечает только за управление счетами (SRP).
работает без TransferService.
 */
public class AccountService {

    private final Repository<AccountNumber, BankAccount> accounts;

    public AccountService(Repository<AccountNumber, BankAccount> accounts) {
        if (accounts == null) {
            throw new IllegalArgumentException("счета null");
        }
        this.accounts = accounts;
    }

    public void open(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("счёт null");
        }
        accounts.save(account);
    }

    public Optional<BankAccount> find(AccountNumber number) {
        return Optional.ofNullable(accounts.findById(number));
    }

    public boolean close(AccountNumber number) {
        return accounts.deleteById(number);
    }
}
package bank.adapters;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.ports.AccountRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// 07.11 этап 5: Adapter (адаптер для порта AccountRepository)
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<AccountNumber, BankAccount> data = new HashMap<>();

    @Override
    public Optional<BankAccount> findById(AccountNumber number) {
        return Optional.ofNullable(data.get(number));
    }

    @Override
    public void save(BankAccount account) {
        data.put(account.getId(), account);
    }
}
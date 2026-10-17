package bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
17.10 этап 4: индекс по счёту на HashMap.
ключ — номер счёта, значение — все его транзакции.
вместо прохода по всему журналу сразу берём готовый список за O(1).
 */
public class TransactionIndex {

    private final Map<AccountNumber, List<Transaction>> byAccount = new HashMap<>();

    public void add(Transaction tx) {
        AccountNumber number = tx.account();
        // computeIfAbsent сам создаст список если ключа ещё нет
        byAccount.computeIfAbsent(number, k -> new ArrayList<>()).add(tx);
    }

    public List<Transaction> findByAccount(AccountNumber number) {
        // копия, чтобы снаружи индекс не поломали
        return new ArrayList<>(byAccount.getOrDefault(number, List.of()));
    }

    public int size() {
        return byAccount.size();
    }
}

package bank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
10.10 этап 3: журнал транзакций.
тут важен порядок и повторы разрешены, поэтому List, а не Set.

10.10 этап 5: удаление через Iterator.
в for-each удалять нельзя — будет ConcurrentModificationException,
поэтому идём через iterator и удаляем через iterator.remove().
 */
public class TransactionJournal {

    private final List<Transaction> transactions = new ArrayList<>();

    public void add(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> findAll() {
        // отдаём копию, чтобы снаружи журнал не поломали
        return new ArrayList<>(transactions);
    }

    public int size() {
        return transactions.size();
    }

    public void removeRejected() {
        // правильный способ удалять при обходе — через Iterator
        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()) {
            Transaction tx = iterator.next();
            if (tx.status() == TransactionStatus.REJECTED) {
                iterator.remove();
            }
        }
    }
}

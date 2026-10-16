package bank;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionJournalTest {

    private Transaction tx(long id, String number) {
        // помощник чтобы не писать одно и то же
        return new Transaction(
                id,
                TransactionType.TRANSFER,
                new AccountNumber(number),
                1000,
                TransactionStatus.SUCCESS
        );
    }

    @Test
    void transactionsKeptInOrder() {
        // подготовка
        TransactionJournal journal = new TransactionJournal();
        Transaction first = tx(1L, "0000000001");
        Transaction second = tx(2L, "0000000002");

        // действие
        journal.add(first);
        journal.add(second);

        // проверка порядок тот же
        List<Transaction> all = journal.findAll();
        assertEquals(2, all.size());
        assertSame(first, all.get(0));
        assertSame(second, all.get(1));
    }

    @Test
    void duplicatesAllowed() {
        // две одинаковые транзакции лежат рядом, это нормально для List
        TransactionJournal journal = new TransactionJournal();
        Transaction tx = tx(1L, "0000000001");
        journal.add(tx);
        journal.add(tx);

        assertEquals(2, journal.size());
    }

    @Test
    void returnedListDoesNotExposeInternalState() {
        // тест из задания
        TransactionJournal journal = new TransactionJournal();
        journal.add(tx(1L, "0000000001"));
        List<Transaction> copy = journal.findAll();
        copy.clear();
        assertEquals(1, journal.size());
    }
}

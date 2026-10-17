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

    @Test
    void removeRejectedDeletesOnlyRejected() {
        // подготовка
        TransactionJournal journal = new TransactionJournal();
        Transaction ok1 = new Transaction(1L, TransactionType.TRANSFER, new AccountNumber("0000000001"), 1000, TransactionStatus.SUCCESS);
        Transaction bad = new Transaction(2L, TransactionType.WITHDRAWAL, new AccountNumber("0000000002"), 500, TransactionStatus.REJECTED);
        Transaction ok2 = new Transaction(3L, TransactionType.TRANSFER, new AccountNumber("0000000003"), 2000, TransactionStatus.SUCCESS);
        journal.add(ok1);
        journal.add(bad);
        journal.add(ok2);

        // действие
        journal.removeRejected();

        // проверка: остались только успешные, в том же порядке
        assertEquals(2, journal.size());
        List<Transaction> all = journal.findAll();
        assertSame(ok1, all.get(0));
        assertSame(ok2, all.get(1));
    }

    @Test
    void removeRejectedEmptyJournal() {
        TransactionJournal journal = new TransactionJournal();
        journal.removeRejected();
        assertEquals(0, journal.size());
    }

    @Test
    void removeRejectedWhenAllRejected() {
        TransactionJournal journal = new TransactionJournal();
        journal.add(new Transaction(1L, TransactionType.TRANSFER, new AccountNumber("0000000001"), 1000, TransactionStatus.REJECTED));
        journal.add(new Transaction(2L, TransactionType.TRANSFER, new AccountNumber("0000000002"), 2000, TransactionStatus.REJECTED));

        journal.removeRejected();

        assertEquals(0, journal.size());
    }

    @Test
    void findByAccountReturnsOnlyMatching() {
        // лежат чужие и свои, вернулись только свои
        TransactionJournal journal = new TransactionJournal();
        Transaction first = tx(1L, "0000000001");
        Transaction other = tx(2L, "0000000002");
        Transaction second = tx(3L, "0000000001");
        journal.add(first);
        journal.add(other);
        journal.add(second);

        List<Transaction> found = journal.findByAccount(new AccountNumber("0000000001"));

        assertEquals(2, found.size());
        assertSame(first, found.get(0));
        assertSame(second, found.get(1));
    }

    @Test
    void findByAccountEmptyWhenNoMatch() {
        // такого счёта в журнале нет — пустой список
        TransactionJournal journal = new TransactionJournal();
        journal.add(tx(1L, "0000000001"));

        List<Transaction> found = journal.findByAccount(new AccountNumber("0000000002"));

        assertTrue(found.isEmpty());
    }
}

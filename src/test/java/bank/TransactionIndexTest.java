package bank;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionIndexTest {

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
    void findByAccountFast() {
        // кладём два счёта, ищем один
        TransactionIndex index = new TransactionIndex();
        Transaction first = tx(1L, "0000000001");
        Transaction other = tx(2L, "0000000002");
        Transaction second = tx(3L, "0000000001");
        index.add(first);
        index.add(other);
        index.add(second);

        List<Transaction> found = index.findByAccount(new AccountNumber("0000000001"));

        assertEquals(2, found.size());
        assertSame(first, found.get(0));
        assertSame(second, found.get(1));
    }

    @Test
    void transactionCanBeFoundByEqualAccountNumber() {
        // тест из задания: разные объекты, но равные номера
        TransactionIndex index = new TransactionIndex();
        AccountNumber stored = new AccountNumber("1111111111");
        AccountNumber lookup = new AccountNumber("1111111111");

        index.add(new Transaction(
                1L, TransactionType.TRANSFER, stored, 1000, TransactionStatus.SUCCESS));

        assertEquals(1, index.findByAccount(lookup).size());
    }

    @Test
    void unknownAccountEmpty() {
        TransactionIndex index = new TransactionIndex();

        assertTrue(index.findByAccount(new AccountNumber("0000000009")).isEmpty());
    }

    @Test
    void journalAndIndexAgree() {
        // одна запись попала и в журнал, и в индекс
        TransactionJournal journal = new TransactionJournal();
        TransactionIndex index = new TransactionIndex();
        Transaction tx = tx(1L, "0000000001");
        journal.add(tx);
        index.add(tx);

        assertEquals(1, journal.size());
        assertEquals(1, index.findByAccount(new AccountNumber("0000000001")).size());
        assertSame(tx, journal.findAll().get(0));
        assertSame(tx, index.findByAccount(new AccountNumber("0000000001")).get(0));
    }

    @Test
    void returnedListDoesNotExposeIndex() {
        // копию из индекса снаружи не поломать
        TransactionIndex index = new TransactionIndex();
        index.add(tx(1L, "0000000001"));
        List<Transaction> copy = index.findByAccount(new AccountNumber("0000000001"));
        copy.clear();

        assertEquals(1, index.findByAccount(new AccountNumber("0000000001")).size());
    }
}

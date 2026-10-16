package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransferQueueTest {

    private TransferRequest req(String from, String to, double amount) {
        // помощник чтобы не писать одно и то же
        return new TransferRequest(
                new AccountNumber(from),
                new AccountNumber(to),
                amount
        );
    }

    @Test
    void nextReturnsInOrderAdded() {
        // подготовка кладём A, B, C
        TransferQueue queue = new TransferQueue();
        TransferRequest a = req("0000000001", "0000000002", 100);
        TransferRequest b = req("0000000003", "0000000004", 200);
        TransferRequest c = req("0000000005", "0000000006", 300);
        queue.add(a);
        queue.add(b);
        queue.add(c);

        // проверка забираем в том же порядке
        assertSame(a, queue.next());
        assertSame(b, queue.next());
        assertSame(c, queue.next());
        assertTrue(queue.isEmpty());
    }

    @Test
    void peekDoesNotRemoveFirst() {
        TransferQueue queue = new TransferQueue();
        TransferRequest a = req("0000000001", "0000000002", 100);
        queue.add(a);

        // peek только смотрит, размер не меняется
        assertSame(a, queue.peek());
        assertEquals(1, queue.size());
        assertSame(a, queue.next());
    }

    @Test
    void nextOnEmptyQueueReturnsNull() {
        TransferQueue queue = new TransferQueue();

        assertNull(queue.next());
        assertNull(queue.peek());
        assertTrue(queue.isEmpty());
    }

    @Test
    void sizeDecreasesAfterPoll() {
        TransferQueue queue = new TransferQueue();
        queue.add(req("0000000001", "0000000002", 100));
        queue.add(req("0000000003", "0000000004", 200));
        assertEquals(2, queue.size());

        queue.next();

        assertEquals(1, queue.size());
    }
}

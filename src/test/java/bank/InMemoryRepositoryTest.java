package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryRepositoryTest {

    @Test
    void saveAndFindById() {
        // подготовка
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BankAccount acc = new DebitAccount("0000000001", "Иван", 1000);

        // действие
        repo.save(acc);

        // проверка
        assertEquals(1, repo.size());
        assertTrue(repo.existsById(new AccountNumber("0000000001")));
        assertEquals(acc, repo.findById(new AccountNumber("0000000001")));
    }

    @Test
    void saveNullThrowsException() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();

        assertThrows(IllegalArgumentException.class, () -> repo.save(null));
    }

    @Test
    void saveSameIdTwiceReplacesOld() {
        // подготовка
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BankAccount first = new DebitAccount("0000000001", "Иван", 1000);
        BankAccount second = new DebitAccount("0000000001", "Иван", 5000);
        repo.save(first);

        // действие сохраняем второй раз с тем же номером
        repo.save(second);

        // проверка дубликата нет, старый заменился
        assertEquals(1, repo.size());
        assertEquals(5000, repo.findById(new AccountNumber("0000000001")).getBalance());
    }

    @Test
    void findByIdThrowsIfNotFound() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();

        assertThrows(
                EntityNotFoundException.class,
                () -> repo.findById(new AccountNumber("0000000009"))
        );
    }

    @Test
    void existsByIdReturnsFalseIfNotFound() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();

        assertFalse(repo.existsById(new AccountNumber("0000000009")));
    }

    @Test
    void worksWithTransactionsToo() {
        // проверка что один интерфейс подходит и для транзакций
        Repository<Long, Transaction> repo = new InMemoryRepository<>();
        Transaction tx = new Transaction(
                1L,
                TransactionType.DEPOSIT,
                new AccountNumber("0000000001"),
                5000,
                TransactionStatus.SUCCESS
        );
        repo.save(tx);

        assertEquals(1, repo.size());
        assertEquals(tx, repo.findById(1L));
    }
}

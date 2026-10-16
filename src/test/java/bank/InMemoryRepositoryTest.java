package bank;

import org.junit.jupiter.api.Test;
import java.util.List;
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
    void savingSameIdReplacesOldValue() {
        // тест из задания: проверяем что вернулся именно новый объект
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        AccountNumber id = new AccountNumber("0000000001");
        BankAccount oldAccount = new DebitAccount(id, "Ivan", 1000);
        BankAccount newAccount = new DebitAccount(id, "Petr", 5000);
        repository.save(oldAccount);
        repository.save(newAccount);
        assertEquals(1, repository.size());
        assertSame(newAccount, repository.findById(id));
    }

    @Test
    void findByIdReturnsNullIfNotFound() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();

        // теперь как у мапы: нет ключа - просто null
        assertNull(repo.findById(new AccountNumber("0000000009")));
    }

    @Test
    void existsByIdReturnsFalseIfNotFound() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();

        assertFalse(repo.existsById(new AccountNumber("0000000009")));
    }

    @Test
    void deleteByIdRemovesAccount() {
        // подготовка
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        repo.save(new DebitAccount("0000000001", "Иван", 1000));

        // действие
        boolean removed = repo.deleteById(new AccountNumber("0000000001"));

        // проверка
        assertTrue(removed);
        assertEquals(0, repo.size());
        assertFalse(repo.existsById(new AccountNumber("0000000001")));
    }

    @Test
    void deleteByIdReturnsFalseIfNotFound() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();

        assertFalse(repo.deleteById(new AccountNumber("0000000009")));
    }

    @Test
    void findAllReturnsAllSaved() {
        // подготовка
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BankAccount first = new DebitAccount("0000000001", "Иван", 1000);
        BankAccount second = new DebitAccount("0000000002", "Петр", 2000);
        repo.save(first);
        repo.save(second);

        // действие
        List<BankAccount> all = repo.findAll();

        // проверка
        assertEquals(2, all.size());
        assertTrue(all.contains(first));
        assertTrue(all.contains(second));
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

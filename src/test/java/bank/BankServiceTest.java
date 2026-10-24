package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Predicate;

public class BankServiceTest {

    private BankService service() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        TransferService transfers = new TransferService(new NoCommission(), new FakeNotificationService());
        return new BankService(repo, transfers);
    }

    @Test
    void openThenFind() {
        // открыли, потом нашли тот же счёт
        BankService service = service();
        BankAccount account = new DebitAccount("0000000001", "Ivan", 1000);

        service.openAccount(account);

        assertSame(account, service.findAccount(new AccountNumber("0000000001")));
    }

    @Test
    void saveSameIdTwiceReplaces() {
        // тот же id второй раз — старый заменяется
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));

        BankAccount updated = new DebitAccount("0000000001", "Petr", 5000);
        service.openAccount(updated);

        BankAccount found = service.findAccount(new AccountNumber("0000000001"));
        assertSame(updated, found);
    }

    @Test
    void closeDeletesAccount() {
        // закрыли — счёт больше не находится
        BankService service = service();
        AccountNumber number = new AccountNumber("0000000001");
        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));

        boolean closed = service.closeAccount(number);

        assertTrue(closed);
        assertNull(service.findAccount(number));
    }

    @Test
    void transferByNumbersOk() {
        // перевод по номерам, оба баланса поменялись
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 10000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 2000));
        AccountNumber from = new AccountNumber("0000000001");
        AccountNumber to = new AccountNumber("0000000002");

        service.transfer(from, to, 3000);

        assertEquals(7000, service.findAccount(from).getBalance());
        assertEquals(5000, service.findAccount(to).getBalance());
    }

    @Test
    void transferMissingFrom() {
        // отправителя нет — получатель не тронут
        BankService service = service();
        service.openAccount(new DebitAccount("0000000002", "Petr", 2000));

        assertThrows(EntityNotFoundException.class, () -> service.transfer(
                new AccountNumber("0000000001"), new AccountNumber("0000000002"), 100));

        assertEquals(2000, service.findAccount(new AccountNumber("0000000002")).getBalance());
    }

    @Test
    void transferMissingTo() {
        // получателя нет — с отправителя ничего не списалось
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 10000));

        assertThrows(EntityNotFoundException.class, () -> service.transfer(
                new AccountNumber("0000000001"), new AccountNumber("0000000002"), 100));

        assertEquals(10000, service.findAccount(new AccountNumber("0000000001")).getBalance());
    }

    @Test
    void transferFromBlocked() {
        // отправитель в блок-листе — деньги на месте
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BlockedAccountRegistry blocked = new BlockedAccountRegistry();
        blocked.block(new AccountNumber("0000000001"));
        FakeNotificationService fake = new FakeNotificationService();
        BankService service = new BankService(
                repo, new TransferService(new NoCommission(), fake, blocked));

        service.openAccount(new DebitAccount("0000000001", "Ivan", 10000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 2000));

        assertThrows(IllegalStateException.class, () -> service.transfer(
                new AccountNumber("0000000001"), new AccountNumber("0000000002"), 1000));

        assertEquals(10000, service.findAccount(new AccountNumber("0000000001")).getBalance());
        assertEquals(2000, service.findAccount(new AccountNumber("0000000002")).getBalance());
    }

    @Test
    void transferNoMoney() {
        // денег не хватает — ни один баланс не поменялся
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 2000));

        assertThrows(InsufficientFundsException.class, () -> service.transfer(
                new AccountNumber("0000000001"), new AccountNumber("0000000002"), 5000));

        assertEquals(1000, service.findAccount(new AccountNumber("0000000001")).getBalance());
        assertEquals(2000, service.findAccount(new AccountNumber("0000000002")).getBalance());
    }

    @Test
    void findAccountsRich() {
        // счета с балансом не меньше 100_000
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 150_000));
        service.openAccount(new DebitAccount("0000000003", "Anna", 99_999));

        Predicate<BankAccount> rich = a -> a.getBalance() >= 100_000;

        List<BankAccount> result = service.findAccounts(rich);

        assertEquals(1, result.size());
        assertSame(service.findAccount(new AccountNumber("0000000002")), result.get(0));
    }

    @Test
    void findAccountsNotBlocked() {
        // незаблокированные счета
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BlockedAccountRegistry blocked = new BlockedAccountRegistry();
        blocked.block(new AccountNumber("0000000001"));
        FakeNotificationService fake = new FakeNotificationService();
        BankService service = new BankService(
                repo, new TransferService(new NoCommission(), fake, blocked));

        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 2000));
        service.openAccount(new DebitAccount("0000000003", "Anna", 3000));

        Predicate<BankAccount> notBlocked = a -> !blocked.isBlocked(a.getId());

        List<BankAccount> result = service.findAccounts(notBlocked);

        assertEquals(2, result.size());
    }

    @Test
    void findAccountsDebitOnly() {
        // только DebitAccount
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));
        service.openAccount(new CreditAccount("0000000002", "Petr", 1000, 5000));
        service.openAccount(new DebitAccount("0000000003", "Anna", 3000));

        Predicate<BankAccount> debit = DebitAccount.class::isInstance;

        List<BankAccount> result = service.findAccounts(debit);

        assertEquals(2, result.size());
    }

    @Test
    void findAccountsRichAndNotBlocked() {
        // богатые И незаблокированные
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BlockedAccountRegistry blocked = new BlockedAccountRegistry();
        blocked.block(new AccountNumber("0000000002"));
        FakeNotificationService fake = new FakeNotificationService();
        BankService service = new BankService(
                repo, new TransferService(new NoCommission(), fake, blocked));

        service.openAccount(new DebitAccount("0000000001", "Ivan", 150_000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 200_000));
        service.openAccount(new DebitAccount("0000000003", "Anna", 50_000));

        Predicate<BankAccount> rich = a -> a.getBalance() >= 100_000;
        Predicate<BankAccount> notBlocked = a -> !blocked.isBlocked(a.getId());
        Predicate<BankAccount> candidates = rich.and(notBlocked);

        List<BankAccount> result = service.findAccounts(candidates);

        assertEquals(1, result.size());
        assertSame(service.findAccount(new AccountNumber("0000000001")), result.get(0));
    }

    @Test
    void findAccountsNotDebit() {
        // не DebitAccount (debit.negate())
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));
        service.openAccount(new CreditAccount("0000000002", "Petr", 1000, 5000));
        service.openAccount(new DebitAccount("0000000003", "Anna", 3000));

        Predicate<BankAccount> debit = DebitAccount.class::isInstance;
        Predicate<BankAccount> notDebit = debit.negate();

        List<BankAccount> result = service.findAccounts(notDebit);

        assertEquals(1, result.size());
        assertSame(service.findAccount(new AccountNumber("0000000002")), result.get(0));
    }

    @Test
    void findAccountsRichOrCredit() {
        // богатые ИЛИ CreditAccount
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 150_000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 500));
        service.openAccount(new CreditAccount("0000000003", "Anna", 0, 5000));

        Predicate<BankAccount> rich = a -> a.getBalance() >= 100_000;
        Predicate<BankAccount> credit = CreditAccount.class::isInstance;
        Predicate<BankAccount> candidates = rich.or(credit);

        List<BankAccount> result = service.findAccounts(candidates);

        assertEquals(2, result.size());
    }
}

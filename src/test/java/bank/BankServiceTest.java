package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Predicate;

import java.util.Comparator;


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

    // 24.10 этап 5: тесты сортировки через Comparator

    @Test
    void sortedByBalanceAscending() {
        // по возрастанию баланса
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 3000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 1000));
        service.openAccount(new DebitAccount("0000000003", "Anna", 2000));

        Comparator<BankAccount> byBalance = Comparator.comparingDouble(BankAccount::getBalance);
        List<BankAccount> sorted = service.sortedAccounts(byBalance);

        assertEquals(1000, sorted.get(0).getBalance());
        assertEquals(2000, sorted.get(1).getBalance());
        assertEquals(3000, sorted.get(2).getBalance());
    }

    @Test
    void sortedByBalanceDescending() {
        // по убыванию баланса
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 3000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 1000));
        service.openAccount(new DebitAccount("0000000003", "Anna", 2000));

        Comparator<BankAccount> byBalance = Comparator.comparingDouble(BankAccount::getBalance);
        Comparator<BankAccount> byBalanceDesc = byBalance.reversed();
        List<BankAccount> sorted = service.sortedAccounts(byBalanceDesc);

        assertEquals(3000, sorted.get(0).getBalance());
        assertEquals(2000, sorted.get(1).getBalance());
        assertEquals(1000, sorted.get(2).getBalance());
    }

    @Test
    void sortedByNumber() {
        // по номеру счёта
        BankService service = service();
        service.openAccount(new DebitAccount("0000000003", "Ivan", 1000));
        service.openAccount(new DebitAccount("0000000001", "Petr", 1000));
        service.openAccount(new DebitAccount("0000000002", "Anna", 1000));

        Comparator<BankAccount> byNumber = Comparator.comparing(a -> a.getId().value());
        List<BankAccount> sorted = service.sortedAccounts(byNumber);

        assertEquals("0000000001", sorted.get(0).getNumber().value());
        assertEquals("0000000002", sorted.get(1).getNumber().value());
        assertEquals("0000000003", sorted.get(2).getNumber().value());
    }

    @Test
    void sortedByTypeThenBalance() {
        // сначала тип (CreditAccount раньше DebitAccount по алфавиту), потом баланс
        BankService service = service();
        BankAccount credit2 = new CreditAccount("0000000001", "Anna", 2000, 5000);
        BankAccount credit1 = new CreditAccount("0000000002", "Ivan", 1000, 5000);
        BankAccount debit = new DebitAccount("0000000003", "Petr", 500);

        service.openAccount(credit2);
        service.openAccount(credit1);
        service.openAccount(debit);

        Comparator<BankAccount> byTypeThenBalance = Comparator
                .comparing((BankAccount a) -> a.getClass().getSimpleName())
                .thenComparingDouble(BankAccount::getBalance);

        List<BankAccount> sorted = service.sortedAccounts(byTypeThenBalance);

        assertSame(credit1, sorted.get(0));
        assertSame(credit2, sorted.get(1));
        assertSame(debit, sorted.get(2));
    }

    @Test
    void sortedAccountsDoesNotModifyRepository() {
        // проверка: сортировка отдаёт копию, внутри банка ничего не сломалось
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 3000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 1000));

        List<BankAccount> sorted = service.sortedAccounts(Comparator.comparingDouble(BankAccount::getBalance));
        sorted.clear();

        assertNotNull(service.findAccount(new AccountNumber("0000000001")));
        assertNotNull(service.findAccount(new AccountNumber("0000000002")));
    }
}

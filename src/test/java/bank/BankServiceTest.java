package bank;

import bank.adapters.ConsoleNotificationAdapter;
import bank.application.AccountService;
import bank.application.TransferApplicationService;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.Transaction;
import bank.ports.NotificationService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Predicate;
import java.util.Optional;
import java.util.Comparator;
import java.util.OptionalDouble;


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

    // 31.10 этап 2: тесты filter через Stream API

    @Test
    void findRichAccountsMatches() {
        BankService service = service();
        // пустой репозиторий -> пустой список
        assertTrue(service.findRichAccounts(100_000).isEmpty());

        service.openAccount(new DebitAccount("0000000001", "Ivan", 50_000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 100_000));
        service.openAccount(new DebitAccount("0000000003", "Anna", 200_000));

        // 0 совпадений
        assertEquals(0, service.findRichAccounts(500_000).size());
        // 1 совпадение
        assertEquals(1, service.findRichAccounts(150_000).size());
        // несколько совпадений
        assertEquals(2, service.findRichAccounts(100_000).size());
    }

    @Test
    void findDebitAndByOwnerStream() {
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));
        service.openAccount(new CreditAccount("0000000002", "Ivan", 2000, 5000));
        service.openAccount(new DebitAccount("0000000003", "Petr", 3000));

        assertEquals(2, service.findDebitAccounts().size());
        assertEquals(2, service.findAccountsByOwner("Ivan").size());
        assertEquals(0, service.findAccountsByOwner("Unknown").size());
    }

    // 31.10 этап 3: тест getAccountSummaries

    @Test
    void getAccountSummariesCorrectlyMapsTypeAndBalance() {
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 1500));

        List<AccountSummary> summaries = service.getAccountSummaries();

        assertEquals(1, summaries.size());
        AccountSummary summary = summaries.get(0);
        assertEquals(new AccountNumber("0000000001"), summary.number());
        assertEquals("DebitAccount", summary.type());
        assertEquals(1500, summary.balance());
    }

    // 31.10 этап 4: тесты stream().sorted()

    @Test
    void richestFirstSortsAndKeepsRepositoryOrderUnchanged() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BankService service = new BankService(
                repo, new TransferService(new NoCommission(), new FakeNotificationService()));

        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 5000));
        service.openAccount(new DebitAccount("0000000003", "Anna", 3000));

        List<BankAccount> beforeSort = repo.findAll();
        List<BankAccount> sorted = service.richestFirst();

        assertEquals(5000, sorted.get(0).getBalance());
        assertEquals(3000, sorted.get(1).getBalance());
        assertEquals(1000, sorted.get(2).getBalance());
        // порядок в самом repository не изменился
        assertEquals(beforeSort, repo.findAll());
    }

    @Test
    void streamSortByNumberTypeAndOwner() {
        BankService service = service();
        BankAccount a1 = new DebitAccount("0000000003", "Petr", 2000);
        BankAccount a2 = new CreditAccount("0000000001", "Ivan", 1000, 5000);
        BankAccount a3 = new DebitAccount("0000000002", "Ivan", 500);
        service.openAccount(a1);
        service.openAccount(a2);
        service.openAccount(a3);

        // по номеру
        List<BankAccount> byNum = service.sortedByNumber();
        assertSame(a2, byNum.get(0));
        assertSame(a3, byNum.get(1));
        assertSame(a1, byNum.get(2));

        // по типу, затем по балансу
        List<BankAccount> byType = service.sortedByTypeThenBalance();
        assertSame(a2, byType.get(0));
        assertSame(a3, byType.get(1));
        assertSame(a1, byType.get(2));

        // по владельцу, затем по номеру
        List<BankAccount> byOwner = service.sortedByOwnerThenNumber();
        assertSame(a2, byOwner.get(0));
        assertSame(a3, byOwner.get(1));
        assertSame(a1, byOwner.get(2));
    }

    // 31.10 этап 5: тесты anyMatch, allMatch, noneMatch и short-circuit эксперимент

    @Test
    void matchOperationsTrueAndFalse() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BlockedAccountRegistry blocked = new BlockedAccountRegistry();
        TransferService transfers = new TransferService(new NoCommission(), new FakeNotificationService(), blocked);
        BankService service = new BankService(repo, transfers, blocked);

        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 2000));

        assertFalse(service.hasBlockedAccounts());
        assertTrue(service.allAccountsPositive());
        assertTrue(service.noCreditAccounts());

        // блокируем первый счёт, добавляем счёт с 0 и кредитку
        blocked.block(new AccountNumber("0000000001"));
        service.openAccount(new DebitAccount("0000000003", "Anna", 0));
        service.openAccount(new CreditAccount("0000000004", "Oleg", 500, 5000));

        assertTrue(service.hasBlockedAccounts());
        assertFalse(service.allAccountsPositive());
        assertFalse(service.noCreditAccounts());
    }

    @Test
    void anyMatchShortCircuitExperiment() {
        // 3 счёта, но подходящий уже на 2-м месте -> 3-й проверяться не должен
        List<BankAccount> list = List.of(
                new DebitAccount("0000000001", "Ivan", 1000),
                new DebitAccount("0000000002", "Petr", 200_000),
                new DebitAccount("0000000003", "Anna", 300_000)
        );
        int[] calls = {0};

        boolean found = list.stream().anyMatch(acc -> {
            calls[0]++;
            return acc.getBalance() >= 100_000;
        });

        assertTrue(found);
        assertEquals(2, calls[0]);
    }

    // 31.10 этап 6: тесты findFirstRichAccount и Optional

    @Test
    void findFirstRichAccountPresentAndEmpty() {
        BankService bank = service();
        bank.openAccount(new DebitAccount("0000000001", "Ivan", 50_000));
        bank.openAccount(new DebitAccount("0000000002", "Petr", 150_000));

        // когда богатый счёт есть
        BankAccount account = bank.findFirstRichAccount(100_000)
                .orElseThrow(() -> new IllegalStateException("No rich account"));
        assertEquals("0000000002", account.getId().value());

        String foundLabel = bank.findFirstRichAccount(100_000)
                .map(acc -> acc.getId().value())
                .orElse("not found");
        assertEquals("0000000002", foundLabel);

        // когда богатого счёта нет (Optional.empty)
        Optional<BankAccount> emptyResult = bank.findFirstRichAccount(500_000);
        assertTrue(emptyResult.isEmpty());

        assertThrows(IllegalStateException.class, () ->
                bank.findFirstRichAccount(500_000)
                        .orElseThrow(() -> new IllegalStateException("No rich account"))
        );

        String notFoundLabel = bank.findFirstRichAccount(500_000)
                .map(acc -> acc.getId().value())
                .orElse("not found");
        assertEquals("not found", notFoundLabel);
    }

    // 31.10 этап 7: тесты totalBalance, averageBalance, richestAccount

    @Test
    void totalBalanceEmptyAndNonEmpty() {
        BankService service = service();
        assertEquals(0.0, service.totalBalance());

        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 3000));

        assertEquals(4000.0, service.totalBalance());
    }

    @Test
    void averageBalanceEmptyAndNonEmpty() {
        BankService service = service();
        assertEquals(OptionalDouble.empty(), service.averageBalance());

        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));
        service.openAccount(new DebitAccount("0000000002", "Petr", 3000));

        assertTrue(service.averageBalance().isPresent());
        assertEquals(2000.0, service.averageBalance().getAsDouble());
    }

    @Test
    void richestAccountMaxAndEmpty() {
        BankService service = service();
        assertTrue(service.richestAccount().isEmpty());

        BankAccount a1 = new DebitAccount("0000000001", "Ivan", 1000);
        BankAccount a2 = new DebitAccount("0000000002", "Petr", 5000);
        service.openAccount(a1);
        service.openAccount(a2);

        Optional<BankAccount> richest = service.richestAccount();
        assertTrue(richest.isPresent());
        assertSame(a2, richest.get());
    }

    // 07.11 этап 2: тесты разделённых AccountService и TransferApplicationService

    @Test
    void accountServiceWorksWithoutTransferService() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        AccountService accountService = new AccountService(repo);
        AccountNumber number = new AccountNumber("0000000001");

        accountService.open(new DebitAccount(number, "Ivan", 1000));

        assertTrue(accountService.find(number).isPresent());
        assertTrue(accountService.close(number));
        assertTrue(accountService.find(number).isEmpty());
    }

    @Test
    void transferApplicationServicePreservesTransferRules() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        AccountService accountService = new AccountService(repo);
        TransferApplicationService transferApp = new TransferApplicationService(
                repo, new TransferService(new PercentCommission(1.0), new FakeNotificationService()));

        AccountNumber from = new AccountNumber("0000000001");
        AccountNumber to = new AccountNumber("0000000002");
        accountService.open(new DebitAccount(from, "Ivan", 10000));
        accountService.open(new DebitAccount(to, "Petr", 2000));

        transferApp.transfer(from, to, 1000);

        assertEquals(8990, accountService.find(from).orElseThrow().getBalance());
        assertEquals(3000, accountService.find(to).orElseThrow().getBalance());
    }

    // 07.11 этап 7: тест паттерна Decorator (цепочка вызовов)
    @Test
    void notificationDecoratorsChain() {
        // Собираем матрёшку: Консоль заворачиваем в Префикс, Префикс в Счётчик, Счётчик в Логгер.
        ConsoleNotificationAdapter console = new ConsoleNotificationAdapter();
        PrefixNotificationDecorator prefixed = new PrefixNotificationDecorator(console, "ВНИМАНИЕ: ");
        CountingNotificationDecorator counter = new CountingNotificationDecorator(prefixed);
        LoggingNotificationDecorator logger = new LoggingNotificationDecorator(counter);

        // Итоговый сервис, который мы передадим в банк
        NotificationService notification = logger;

        notification.notify("Перевод выполнен");
        notification.notify("Счёт закрыт");

        // Проверяем, что счётчик реально насчитал 2 вызова, проходя через всю цепочку
        assertEquals(2, counter.getCount());
    }

    // 07.11 этап 8: тест паттерна Observer (несколько подписчиков)
    @Test
    void transferEventPublisherNotifiesAllListeners() {
        TransferEventPublisher publisher = new TransferEventPublisher();
        int[] notifiedCount = {0};

        publisher.subscribe(tx -> notifiedCount[0]++);
        publisher.subscribe(tx -> notifiedCount[0]++);

        Transaction tx = new Transaction(
                1L, TransactionType.TRANSFER, new AccountNumber("0000000001"), 500, TransactionStatus.SUCCESS);
        publisher.publish(tx);

        assertEquals(2, notifiedCount[0]);
    }

    // 07.11 этап 10: проверка LSP (все наследники работают как BankAccount без instanceof)
    @Test
    void liskovSubstitutionPrincipleForAllAccounts() {
        List<BankAccount> accounts = List.of(
                new DebitAccount("0000000001", "Ivan", 5000),
                new SavingsAccount("0000000002", "Petr", 5000, 1000),
                new CreditAccount("0000000003", "Anna", 0, 5000)
        );

        for (BankAccount account : accounts) {
            account.deposit(500);
            account.withdraw(1000);
        }

        assertEquals(4500, accounts.get(0).getBalance());
        assertEquals(4500, accounts.get(1).getBalance());
        assertEquals(-500, accounts.get(2).getBalance());
    }
}

package bank;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.Optional;
import java.util.OptionalDouble;

/*
17.10 этап 1: сервис владеет счетами.
клиент ходит сюда, а не в Repository напрямую.

07.11 этап 1: архитектурная диагностика (нарушение SRP).
Причины для изменения BankService сейчас смешаны в одном классе:
1. управление счетами (openAccount, findAccount, closeAccount);
2. переводы между счетами (transfer);
3. отчёты и аналитика (totalBalance, averageBalance, richestAccount, getAccountSummaries);
4. фильтрация и сортировка (findAccounts, sortedAccounts).
 */
public class BankService {

    private final Repository<AccountNumber, BankAccount> accounts;
    private final TransferService transferService;
    private final BlockedAccountRegistry blockedAccounts;

    public BankService(
            Repository<AccountNumber, BankAccount> accounts,
            TransferService transferService) {
        this(accounts, transferService, new BlockedAccountRegistry());
    }

    public BankService(
            Repository<AccountNumber, BankAccount> accounts,
            TransferService transferService,
            BlockedAccountRegistry blockedAccounts) {
        if (accounts == null) {
            throw new IllegalArgumentException("счета null");
        }
        if (transferService == null) {
            throw new IllegalArgumentException("переводы null");
        }
        if (blockedAccounts == null) {
            throw new IllegalArgumentException("блокировки null");
        }
        this.accounts = accounts;
        this.transferService = transferService;
        this.blockedAccounts = blockedAccounts;
    }

    public void openAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("счёт null");
        }
        accounts.save(account);
    }

    public BankAccount findAccount(AccountNumber number) {
        return accounts.findById(number);
    }

    public boolean closeAccount(AccountNumber number) {
        return accounts.deleteById(number);
    }

    // 17.10 этап 2: перевод по номерам, сами счета достаём внутри
    public void transfer(AccountNumber fromNumber, AccountNumber toNumber, double amount) {
        BankAccount from = accounts.findById(fromNumber);
        if (from == null) {
            throw new EntityNotFoundException("счёт не найден");
        }
        BankAccount to = accounts.findById(toNumber);
        if (to == null) {
            throw new EntityNotFoundException("счёт не найден");
        }
        transferService.transfer(from, to, amount);
    }

    // 24.10 этап 3: ищем счета по условию, не показываем внутреннее хранилище
    public List<BankAccount> findAccounts(Predicate<? super BankAccount> condition) {
        List<BankAccount> all = accounts.findAll();
        return CollectionFunctions.filter(all, condition);
    }

    // 24.10 этап 5: сортировка счетов через переданный компаратор
    public List<BankAccount> sortedAccounts(Comparator<? super BankAccount> comparator) {
        List<BankAccount> result = new ArrayList<>(accounts.findAll());
        result.sort(comparator);
        return result;
    }

    // 31.10 этап 2: отбор счетов через Stream API
    public List<BankAccount> findRichAccounts(double minBalance) {
        return accounts.findAll().stream()
                .filter(acc -> acc.getBalance() >= minBalance)
                .toList();
    }

    public List<BankAccount> findDebitAccounts() {
        return accounts.findAll().stream()
                .filter(DebitAccount.class::isInstance)
                .toList();
    }

    public List<BankAccount> findAccountsByOwner(String owner) {
        return accounts.findAll().stream()
                .filter(acc -> acc.getOwner().equals(owner))
                .toList();
    }

    // 31.10 этап 3: преобразование в AccountSummary через map и method reference
    public List<AccountSummary> getAccountSummaries() {
        return accounts.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    private AccountSummary toSummary(BankAccount account) {
        return new AccountSummary(
                account.getId(),
                account.getClass().getSimpleName(),
                account.getBalance()
        );
    }

    // 31.10 этап 4: сортировка через stream().sorted()
    public List<BankAccount> richestFirst() {
        return accounts.findAll().stream()
                .sorted(Comparator
                        .comparingDouble(BankAccount::getBalance)
                        .reversed())
                .toList();
    }

    public List<BankAccount> sortedByNumber() {
        return accounts.findAll().stream()
                .sorted(Comparator.comparing(acc -> acc.getId().value()))
                .toList();
    }

    public List<BankAccount> sortedByTypeThenBalance() {
        return accounts.findAll().stream()
                .sorted(Comparator
                        .comparing((BankAccount acc) -> acc.getClass().getSimpleName())
                        .thenComparingDouble(BankAccount::getBalance))
                .toList();
    }

    public List<BankAccount> sortedByOwnerThenNumber() {
        return accounts.findAll().stream()
                .sorted(Comparator
                        .comparing(BankAccount::getOwner)
                        .thenComparing(acc -> acc.getId().value()))
                .toList();
    }

    // 31.10 этап 5: short-circuit операции
    public boolean hasBlockedAccounts() {
        return accounts.findAll().stream()
                .anyMatch(acc -> blockedAccounts.isBlocked(acc.getId()));
    }

    public boolean allAccountsPositive() {
        return accounts.findAll().stream()
                .allMatch(acc -> acc.getBalance() > 0);
    }

    public boolean noCreditAccounts() {
        return accounts.findAll().stream()
                .noneMatch(CreditAccount.class::isInstance);
    }

    // 31.10 этап 6: findFirst и Optional
    public Optional<BankAccount> findFirstRichAccount(double minBalance) {
        return accounts.findAll().stream()
                .filter(acc -> acc.getBalance() >= minBalance)
                .findFirst();
    }

    // 31.10 этап 7: числовые stream (sum, average, max)
    public double totalBalance() {
        return accounts.findAll().stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();
    }

    public OptionalDouble averageBalance() {
        return accounts.findAll().stream()
                .mapToDouble(BankAccount::getBalance)
                .average();
    }

    public Optional<BankAccount> richestAccount() {
        return accounts.findAll().stream()
                .max(Comparator.comparingDouble(BankAccount::getBalance));
    }
}
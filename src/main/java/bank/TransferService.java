package bank;

import bank.domain.BankAccount;
import bank.domain.CommissionPolicy;
import bank.domain.Transaction;
import bank.ports.NotificationService;

@Audited
public class TransferService {

    // максимальная сумма одного перевода
    private static final double TRANSFER_LIMIT = 50_000;

    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;
    private final BlockedAccountRegistry blockedAccounts;
    // журнал может быть null, тогда просто не пишем
    private final TransactionJournal journal;

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
        // старый конструктор оставляем, просто реестр пустой
        this.blockedAccounts = new BlockedAccountRegistry();
        this.journal = null;
    }

    public TransferService(
            CommissionPolicy commissionPolicy,
            NotificationService notificationService,
            BlockedAccountRegistry blockedAccounts) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
        this.blockedAccounts = blockedAccounts;
        this.journal = null;
    }

    public TransferService(
            CommissionPolicy commissionPolicy,
            NotificationService notificationService,
            BlockedAccountRegistry blockedAccounts,
            TransactionJournal journal) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
        this.blockedAccounts = blockedAccounts;
        this.journal = journal;
    }

    public void transfer(BankAccount from, BankAccount to, double amount) {
        // сначала все проверки, потом уже движение денег
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (from == null || to == null) {
            throw new IllegalArgumentException("Accounts must not be null");
        }

        if (from.equals(to)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        // с заблокированного счёта переводить нельзя
        if (blockedAccounts.isBlocked(from.getNumber())) {
            throw new IllegalStateException("Source account is blocked");
        }

        if (amount > TRANSFER_LIMIT) {
            throw new TransferLimitExceededException("Transfer limit exceeded");
        }

        // снимаем с отправителя сумму вместе с комиссией,
        // если денег не хватит - InsufficientFundsException уйдёт наружу
        // и до получателя мы не дойдём

        // 21.11 этап 4: перевод как целостная операция (блокируем оба счёта)
        double commission = commissionPolicy.calculate(amount);
        /*
        21.11 этап 5: эксперимент с deadlock (client-4: 04->05, client-5: 05->04).
        Последовательность circular wait:
        Thread 1 (client-4) locked 04
        Thread 2 (client-5) locked 05
        Thread 1 waits for 05
        Thread 2 waits for 04
        Оба потока держат по одному счёту и бесконечно ждут второй -> зависание.
         */
        synchronized (from) {
            synchronized (to) {
                from.withdraw(amount + commission);
                to.deposit(amount);
            }
        }

        // пишем в журнал только если перевод реально прошёл
        if (journal != null) {
            journal.add(new Transaction(
                    null,
                    TransactionType.TRANSFER,
                    from.getNumber(),
                    amount,
                    TransactionStatus.SUCCESS
            ));
        }

        notificationService.notify("Transfer " + amount + " completed");
    }
}
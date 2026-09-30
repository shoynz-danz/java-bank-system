package bank;

public class TransferService {

    // максимальная сумма одного перевода
    private static final double TRANSFER_LIMIT = 50_000;

    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
    }

    public void transfer(BankAccount from, BankAccount to, double amount) {
        // сначала все проверки, потом уже движение денег
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (from == null || to == null) {
            throw new IllegalArgumentException("Accounts must not be null");
        }

        if (from == to) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        if (amount > TRANSFER_LIMIT) {
            throw new TransferLimitExceededException("Transfer limit exceeded");
        }

        // снимаем с отправителя сумму вместе с комиссией,
        // если денег не хватит - InsufficientFundsException уйдёт наружу
        // и до получателя мы не дойдём
        double commission = commissionPolicy.calculate(amount);
        from.withdraw(amount + commission);

        // деньги зачисляем только после успешного списания
        to.deposit(amount);

        notificationService.notify("Transfer " + amount + " completed");
    }
}
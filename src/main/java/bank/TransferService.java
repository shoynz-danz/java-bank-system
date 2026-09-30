package bank;

public class TransferService {
    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
    }

    public boolean transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0 || from == null || to == null || from == to) {
            return false;
        }

        double commission = commissionPolicy.calculate(amount);
        double totalToWithdraw = amount + commission;

        try {
            from.withdraw(totalToWithdraw);
        } catch (InsufficientFundsException | IllegalArgumentException e) {
            return false;
        }

        to.deposit(amount);

        notificationService.notify("Transfer " + amount + " completed");

        return true;
    }
}

/*
ОБНОВЛЕННЫЙ КОД для ЭТАП 5
Он должен получать обе зависимости через конструктор:

public TransferService(
        CommissionPolicy commissionPolicy,
        NotificationService notificationService) {
    ...
}

После успешного перевода должно отправляться уведомление.

При неуспешном переводе уведомление отправляться не должно.
 */
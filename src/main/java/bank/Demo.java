package bank;

/*
14 ЭТАП: простой пример.
Исключения ловим тут, в точке входа, а не внутри бизнес-классов.
TransferService просто бросает исключение наружу и не печатает ничего.
 */
public class Demo {

    public static void main(String[] args) {
        CommissionPolicy commission = new PercentCommission(1.0);
        NotificationService notifications = new ConsoleNotificationService();
        TransferService transferService = new TransferService(commission, notifications);

        BankAccount from = new DebitAccount("0000000001", "Ivan", 10000);
        BankAccount to = new DebitAccount("0000000002", "Petr", 2000);

        // 1. успешный перевод
        try {
            transferService.transfer(from, to, 5000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
        System.out.println("from: " + from.getBalance());
        System.out.println("to: " + to.getBalance());

        // 2. денег не хватит
        try {
            transferService.transfer(from, to, 5000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        // 3. сумма выше лимита
        try {
            transferService.transfer(from, to, 60000);
            System.out.println("Transfer completed");
        } catch (TransferLimitExceededException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        // 4. некорректные аргументы
        try {
            transferService.transfer(from, to, -500);
            System.out.println("Transfer completed");
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        // после всех ошибок балансы целые
        System.out.println("from: " + from.getBalance());
        System.out.println("to: " + to.getBalance());
    }
}

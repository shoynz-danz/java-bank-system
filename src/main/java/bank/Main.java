package bank;

public class Main {
    public static void main(String[] args) {
        // дебетовый и накопительный
        DebitAccount debit = new DebitAccount("1", "Ivan", 10000);
        System.out.println(debit.withdraw(8000)); // тру
        System.out.println("дебет: " + debit.getBalance());
        System.out.println(debit.withdraw(3000));
        System.out.println("дебет: " + debit.getBalance()); // ложь

        SavingsAccount savings = new SavingsAccount("2", "Petr", 10000, 1000);
        System.out.println(savings.withdraw(8500));
        System.out.println(savings.withdraw(1000));
        System.out.println("накоп: " + savings.getBalance());

        // кредитка
        CreditAccount credit = new CreditAccount("3", "Anna", 1000, 5000);
        System.out.println(credit.withdraw(4000)); // тру, ушли в -3000
        System.out.println("кредитка: " + credit.getBalance());
        System.out.println(credit.withdraw(3000)); // ложь, превысили лимит
        System.out.println("кредитка: " + credit.getBalance());

        // переводы и комиссия
        TransferService service = new TransferService(
                new PercentCommission(1.0), // комиссия 1 проц
                new ConsoleNotificationService()
        );

        DebitAccount acc1 = new DebitAccount("4", "Alex", 11000);
        DebitAccount acc2 = new DebitAccount("5", "Bob", 2000);

        System.out.println(service.transfer(acc1, acc2, 3000)); // тру
        System.out.println("acc1: " + acc1.getBalance());
        System.out.println("acc2: " + acc2.getBalance());

        System.out.println(service.transfer(acc1, acc2, 20000)); // ложь
    }
}


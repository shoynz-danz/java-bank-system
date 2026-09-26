package bank;

public class Main {
    public static void main(String[] args) {
        // дебетовый и накопительный
        DebitAccount debit = new DebitAccount("0000000001", "Ivan", 10000);
        System.out.println(debit.withdraw(8000)); // тру
        System.out.println("дебет: " + debit.getBalance());
        System.out.println(debit.withdraw(3000));
        System.out.println("дебет: " + debit.getBalance()); // ложь

        SavingsAccount savings = new SavingsAccount("0000000002", "Petr", 10000, 1000);
        System.out.println(savings.withdraw(8500));
        System.out.println(savings.withdraw(1000));
        System.out.println("накоп: " + savings.getBalance());

        // кредитка
        CreditAccount credit = new CreditAccount("0000000003", "Anna", 1000, 5000);
        System.out.println(credit.withdraw(4000)); // тру, ушли в -3000
        System.out.println("кредитка: " + credit.getBalance());
        System.out.println(credit.withdraw(3000)); // ложь, превысили лимит
        System.out.println("кредитка: " + credit.getBalance());

        // переводы и комиссия
        TransferService service = new TransferService(
                new PercentCommission(1.0), // комиссия 1 проц
                new ConsoleNotificationService()
        );

        DebitAccount acc1 = new DebitAccount("0000000004", "Alex", 11000);
        DebitAccount acc2 = new DebitAccount("0000000005", "Bob", 2000);

        System.out.println(service.transfer(acc1, acc2, 3000)); // тру
        System.out.println("acc1: " + acc1.getBalance());
        System.out.println("acc2: " + acc2.getBalance());

        System.out.println(service.transfer(acc1, acc2, 20000)); // ложь

        System.out.println("----------------------");
        System.out.println("26.09 1 этап:");
        System.out.println(debit);

        System.out.println("----------------------");
        System.out.println("26.09: проверка equals и hashCode");
        BankAccount a = new DebitAccount("0000000001", "лёха", 1000);
        BankAccount b = new CreditAccount("0000000001", "Пятачок", 5000, 2000);
        BankAccount c = new DebitAccount("0000000002", "Кабачок", 1000);
        BankAccount s = new SavingsAccount("0000000001", "Кент", 1000, 500);

        System.out.println(a.equals(b)); //  дебет == кредит
        System.out.println(a.equals(s)); // дебет == копилка
        System.out.println(b.equals(s)); // кредит == копилка
        System.out.println(a.equals(c)); //тразные номера счетов
        System.out.println(a.hashCode() == b.hashCode());

        // Проверка Этапа 4:
        System.out.println("----------------------");
        AccountNumber num = new AccountNumber("1234567890");
        System.out.println("Номер валидный: " + num.value());
        // new AccountNumber("123"); ошибка уже

        System.out.println("----------------------");
        System.out.println("26.09 7 этап: транзакции");

        Transaction tx1 = new Transaction(
                TransactionType.DEPOSIT,
                new AccountNumber("1234567890"),
                5000,
                TransactionStatus.SUCCESS
        );

        Transaction tx2 = new Transaction(
                TransactionType.WITHDRAWAL,
                new AccountNumber("0000000001"),
                3000,
                TransactionStatus.REJECTED
        );

        System.out.println(tx1);
        System.out.println(tx2);
    }
}


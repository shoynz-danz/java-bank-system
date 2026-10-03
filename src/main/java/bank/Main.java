package bank;

public class Main {
    public static void main(String[] args) {
        // дебетовый и накопительный
        DebitAccount debit = new DebitAccount("0000000001", "Ivan", 10000);
        debit.withdraw(8000); // просто вызываем
        System.out.println("дебет: " + debit.getBalance());

        try {
            debit.withdraw(3000); // тут упадет ошибка
        } catch (InsufficientFundsException e) {
            System.out.println("дебет: не хватило денег!");
        }

        SavingsAccount savings = new SavingsAccount("0000000002", "Petr", 10000, 1000);
        savings.withdraw(8500);
        try {
            savings.withdraw(1000); // не хватит из-за неснижаемого остатка
        } catch (InsufficientFundsException e) {
            System.out.println("накоп: не хватило денег!");
        }
        System.out.println("накоп: " + savings.getBalance());

        // кредитка
        CreditAccount credit = new CreditAccount("0000000003", "Anna", 1000, 5000);
        credit.withdraw(4000); // ушли в -3000
        System.out.println("кредитка: " + credit.getBalance());

        try {
            credit.withdraw(3000); // превысит лимит 5000
        } catch (InsufficientFundsException e) {
            System.out.println("кредитка: лимит превыше");
        }
        System.out.println("кредитка: " + credit.getBalance());

        System.out.println("----------------------");
        System.out.println("26.09 11 этап: переводы");
        // переводы и комиссия
        TransferService service = new TransferService(
                new PercentCommission(1.0), // комиссия 1 проц
                new ConsoleNotificationService()
        );

        DebitAccount acc1 = new DebitAccount("0000000004", "Alex", 11000);
        DebitAccount acc2 = new DebitAccount("0000000005", "Bob", 2000);

        try {
            service.transfer(acc1, acc2, 3000); // тут всё ок
            System.out.println("перевод прошёл");
        } catch (RuntimeException e) {
            System.out.println("перевод не прошёл: " + e.getMessage());
        }
        System.out.println("acc1: " + acc1.getBalance());
        System.out.println("acc2: " + acc2.getBalance());

        try {
            service.transfer(acc1, acc2, -500); // отрицательная сумма
        } catch (IllegalArgumentException e) {
            System.out.println("перевод не прошёл: " + e.getMessage());
        }

        try {
            service.transfer(acc1, acc1, 1000); // себе самому
        } catch (IllegalArgumentException e) {
            System.out.println("перевод не прошёл: " + e.getMessage());
        }

        try {
            service.transfer(acc1, acc2, 20000); // не хватит денег
        } catch (InsufficientFundsException e) {
            System.out.println("перевод не прошёл: " + e.getMessage());
        }

        try {
            service.transfer(acc1, acc2, 60000); // выше лимита 50 000
        } catch (TransferLimitExceededException e) {
            System.out.println("перевод не прошёл: " + e.getMessage());
        }
        // после всех ошибок баланс acc1 не изменился
        System.out.println("acc1: " + acc1.getBalance());
        System.out.println("acc2: " + acc2.getBalance());

        System.out.println("----------------------");
        System.out.println("26.09 13 этап: состояние после ошибки");
        TransferService service2 = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        DebitAccount acc3 = new DebitAccount("0000000006", "Ivan", 1000);
        DebitAccount acc4 = new DebitAccount("0000000007", "Petr", 2000);

        System.out.println("до ошибки acc3: " + acc3.getBalance());
        System.out.println("до ошибки acc4: " + acc4.getBalance());

        try {
            service2.transfer(acc3, acc4, 5000); // денег не хватит
        } catch (InsufficientFundsException e) {
            System.out.println("перевод не прошёл: " + e.getMessage());
        }

        // балансы остались как были, система не сломалась
        System.out.println("после ошибки acc3: " + acc3.getBalance());
        System.out.println("после ошибки acc4: " + acc4.getBalance());

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

        // 03.10 BOX
        //  воркает
        Box box1 = new Box();
        box1.set("Java");
        String value1 = (String) box1.get();
        System.out.println("Сценарий 1: " + value1);

        // падает во время выполнения с ClassCastException
        Box box2 = new Box();
        box2.set(123); // положили число Integer

        try {
            String value2 = (String) box2.get(); // тут и падает
            System.out.println("Сценарий 2: " + value2);
        } catch (ClassCastException e) {
            System.out.println("Сценарий 2: упало с " + e.getMessage());
        }
    }
}


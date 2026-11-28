package bank;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// 28.11 этап 1: запуск 1000 заявок через пул потоков
public class ConcurrentBankApplication {

    public static void main(String[] args) throws InterruptedException {
        BankService bank = createBank();
        double initialTotal = bank.totalBalance();

        List<TransferOrder> orders = generateOrders(1000);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        TransferDispatcher dispatcher = new TransferDispatcher(bank, executor);

        try {
            for (TransferOrder order : orders) {
                dispatcher.dispatch(order);
            }
        } finally {
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        }

        double finalTotal = bank.totalBalance();
        System.out.println("submitted: " + dispatcher.getSubmitted());
        System.out.println("completed: " + dispatcher.getCompleted());
        System.out.println("success: " + bank.successfulTransfers() + ", failed: " + bank.failedTransfers());
        System.out.println("total: " + initialTotal + " -> " + finalTotal);
    }

    static BankService createBank() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        TransferService transferService = new TransferService(new NoCommission(), msg -> {});
        BankService bank = new BankService(repo, transferService);

        bank.openAccount(new DebitAccount("0000000001", "Ivan", 100_000));
        bank.openAccount(new DebitAccount("0000000002", "Anna", 100_000));
        bank.openAccount(new DebitAccount("0000000003", "Petr", 100_000));
        bank.openAccount(new DebitAccount("0000000004", "Maria", 100_000));
        bank.openAccount(new DebitAccount("0000000005", "Alex", 100_000));
        return bank;
    }

    static List<TransferOrder> generateOrders(int count) {
        AccountNumber[] accs = {
                new AccountNumber("0000000001"),
                new AccountNumber("0000000002"),
                new AccountNumber("0000000003"),
                new AccountNumber("0000000004"),
                new AccountNumber("0000000005")
        };
        AccountNumber badAcc = new AccountNumber("9999999999");

        List<TransferOrder> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            AccountNumber from = accs[i % accs.length];
            AccountNumber to = accs[(i + 1) % accs.length];

            if (i % 50 == 0) {
                list.add(new TransferOrder(i, from, to, -100));
            } else if (i % 50 == 1) {
                list.add(new TransferOrder(i, from, badAcc, 50));
            } else if (i % 50 == 2) {
                list.add(new TransferOrder(i, from, to, 200_000));
            } else {
                list.add(new TransferOrder(i, from, to, 10));
            }
        }
        return list;
    }
}
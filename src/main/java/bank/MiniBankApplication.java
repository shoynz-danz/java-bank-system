package bank;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.ports.NotificationService;

// 21.11 этап 11: полная симуляция (5 клиентов + 2 монитора) и финальный отчёт
public class MiniBankApplication {

    public static void main(String[] args) throws InterruptedException {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        NotificationService silent = message -> {};
        TransferService transferService = new TransferService(new NoCommission(), silent);
        BankService bank = new BankService(repo, transferService);

        // 1. Создаём 5 счетов по 100 000 -> TOTAL = 500 000
        bank.openAccount(new DebitAccount("0000000001", "Ivan", 100_000));
        bank.openAccount(new DebitAccount("0000000002", "Anna", 100_000));
        bank.openAccount(new DebitAccount("0000000003", "Petr", 100_000));
        bank.openAccount(new DebitAccount("0000000004", "Maria", 100_000));
        bank.openAccount(new DebitAccount("0000000005", "Alex", 100_000));

        double initialTotal = bank.totalBalance();

        // 2. Создаём два фоновых процесса
        BankMonitor bankMonitor = new BankMonitor(bank, initialTotal);
        StatisticsMonitor statisticsMonitor = new StatisticsMonitor(bank);

        Thread bankMonitorThread = new Thread(bankMonitor, "bank-monitor");
        Thread statisticsThread = new Thread(statisticsMonitor, "statistics-monitor");

        // 3. Создаём 5 клиентских потоков
        Thread client1 = new Thread(new ClientWorker(bank, "0000000001", "0000000002", 10_000), "client-1");
        Thread client2 = new Thread(new ClientWorker(bank, "0000000002", "0000000003", 10_000), "client-2");
        Thread client3 = new Thread(new ClientWorker(bank, "0000000003", "0000000001", 10_000), "client-3");
        Thread client4 = new Thread(new ClientWorker(bank, "0000000004", "0000000005", 10_000), "client-4");
        Thread client5 = new Thread(new ClientWorker(bank, "0000000005", "0000000004", 10_000), "client-5");

        // 4. Запускаем фоновые мониторы
        bankMonitorThread.start();
        statisticsThread.start();

        // 5. Запускаем клиентов
        client1.start();
        client2.start();
        client3.start();
        client4.start();
        client5.start();

        // 6. Ждём завершения всех клиентов
        client1.join();
        client2.join();
        client3.join();
        client4.join();
        client5.join();

        // 7. Останавливаем оба фоновых монитора (graceful shutdown)
        bankMonitor.stop();
        statisticsMonitor.stop();

        bankMonitorThread.interrupt();
        statisticsThread.interrupt();

        // 8. Ждём корректного завершения фоновых потоков
        bankMonitorThread.join();
        statisticsThread.join();

        // 9. Финальный отчёт
        double finalTotal = bank.totalBalance();

        System.out.println();
        System.out.println("=== FINAL REPORT ===");
        System.out.println("Initial total:        " + initialTotal);
        System.out.println("Final total:          " + finalTotal);
        System.out.println("Successful transfers: " + bank.successfulTransfers());
        System.out.println("Failed transfers:     " + bank.failedTransfers());

        if (Double.compare(initialTotal, finalTotal) == 0) {
            System.out.println("Bank invariant: OK");
        } else {
            System.out.println("Bank invariant: BROKEN");
        }
        System.out.println("All workers stopped.");
    }
}
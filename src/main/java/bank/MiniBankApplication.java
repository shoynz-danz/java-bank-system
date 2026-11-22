package bank;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.ports.NotificationService;

// 21.11 этап 8: запуск BankMonitor и graceful shutdown
public class MiniBankApplication {

    public static void main(String[] args) throws InterruptedException {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        NotificationService silent = message -> {};
        TransferService transferService = new TransferService(new NoCommission(), silent);
        BankService bank = new BankService(repo, transferService);

        bank.openAccount(new DebitAccount("0000000001", "Ivan", 100_000));
        bank.openAccount(new DebitAccount("0000000002", "Anna", 100_000));
        bank.openAccount(new DebitAccount("0000000003", "Petr", 100_000));
        bank.openAccount(new DebitAccount("0000000004", "Maria", 100_000));
        bank.openAccount(new DebitAccount("0000000005", "Alex", 100_000));

        double initialTotal = bank.totalBalance();

        BankMonitor bankMonitor = new BankMonitor(bank, initialTotal);
        Thread bankMonitorThread = new Thread(bankMonitor, "bank-monitor");

        Thread client1 = new Thread(new ClientWorker(bank, "0000000001", "0000000002", 10_000), "client-1");
        Thread client2 = new Thread(new ClientWorker(bank, "0000000002", "0000000003", 10_000), "client-2");
        Thread client3 = new Thread(new ClientWorker(bank, "0000000003", "0000000001", 10_000), "client-3");
        Thread client4 = new Thread(new ClientWorker(bank, "0000000004", "0000000005", 10_000), "client-4");
        Thread client5 = new Thread(new ClientWorker(bank, "0000000005", "0000000004", 10_000), "client-5");

        bankMonitorThread.start();

        client1.start();
        client2.start();
        client3.start();
        client4.start();
        client5.start();

        client1.join();
        client2.join();
        client3.join();
        client4.join();
        client5.join();

        // graceful shutdown монитора
        bankMonitor.stop();
        bankMonitorThread.interrupt();
        bankMonitorThread.join();

        double finalTotal = bank.totalBalance();
        System.out.println("Initial total: " + initialTotal);
        System.out.println("Final total:   " + finalTotal);
        System.out.println("Invariant OK:  " + (Double.compare(initialTotal, finalTotal) == 0));
    }
}
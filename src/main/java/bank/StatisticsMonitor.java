package bank;

// 21.11 этап 10: второй фоновый поток для вывода статистики переводов
public class StatisticsMonitor implements Runnable {

    private final BankService bank;
    private volatile boolean running = true;

    public StatisticsMonitor(BankService bank) {
        this.bank = bank;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();

        while (running && !Thread.currentThread().isInterrupted()) {
            int success = bank.successfulTransfers();
            int failed = bank.failedTransfers();
            double seconds = (System.nanoTime() - startTime) / 1_000_000_000.0;
            long opsPerSec = seconds > 0 ? Math.round((success + failed) / seconds) : 0;

            System.out.println("[STATISTICS]");
            System.out.println("success = " + success);
            System.out.println("failed = " + failed);
            System.out.println("operations/sec ~= " + opsPerSec);

            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
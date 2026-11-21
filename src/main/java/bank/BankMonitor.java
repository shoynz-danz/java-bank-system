package bank;

// 21.11 этап 7-8: фоновый монитор инварианта банка с корректной остановкой
public class BankMonitor implements Runnable {

    private final BankService bank;
    private final double expectedTotal;
    private volatile boolean running = true;

    public BankMonitor(BankService bank, double expectedTotal) {
        this.bank = bank;
        this.expectedTotal = expectedTotal;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            double total = bank.totalBalance();
            if (Double.compare(total, expectedTotal) == 0) {
                System.out.println("[MONITOR] total=" + total + " OK");
            } else {
                System.out.println("[MONITOR] total=" + total + " ERROR");
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
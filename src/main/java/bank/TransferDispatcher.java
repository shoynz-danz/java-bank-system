package bank;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

// 28.11 этап 1: диспетчер заявок, пул передаётся снаружи
public class TransferDispatcher {

    private final BankService bank;
    private final ExecutorService executor;
    private final AtomicInteger submitted = new AtomicInteger();
    private final AtomicInteger completed = new AtomicInteger();

    public TransferDispatcher(BankService bank, ExecutorService executor) {
        this.bank = bank;
        this.executor = executor;
    }

    public void dispatch(TransferOrder order) {
        submitted.incrementAndGet();
        executor.execute(() -> {
            if (order.id() <= 10) {
                System.out.println("заявка " + order.id() + " -> " + Thread.currentThread().getName());
            }
            try {
                bank.transfer(order.from(), order.to(), order.amount());
            } catch (RuntimeException ignored) {
            } finally {
                completed.incrementAndGet();
            }
        });
    }

    public int getSubmitted() {
        return submitted.get();
    }

    public int getCompleted() {
        return completed.get();
    }
}
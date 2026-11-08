package bank;

import bank.domain.Transaction;

import java.util.ArrayList;
import java.util.List;

// 07.11 этап 8: Observer (уведомляет всех подписчиков о переводе)
public class TransferEventPublisher {

    private final List<TransferListener> listeners = new ArrayList<>();

    public void subscribe(TransferListener listener) {
        listeners.add(listener);
    }

    public void publish(Transaction tx) {
        for (TransferListener listener : listeners) {
            listener.onTransferCompleted(tx);
        }
    }
}
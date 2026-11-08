package bank;

import bank.domain.Transaction;

// 07.11 этап 8: Observer (подписчик на событие перевода)
@FunctionalInterface
public interface TransferListener {
    void onTransferCompleted(Transaction transaction);
}
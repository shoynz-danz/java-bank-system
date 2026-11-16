package bank;

import bank.domain.Transaction;

// 14.11 этап 1: DTO для внешнего представления транзакции
public record TransactionDto(
        long id,
        String from,
        String to,
        double amount,
        String status
) {
    public static TransactionDto fromDomain(Transaction tx) {
        long txId = tx.id() != null ? tx.id() : 0L;
        String from = tx.account().value();
        String to = tx.type() != null ? tx.type().name() : "";
        return new TransactionDto(
                txId,
                from,
                to,
                tx.amount(),
                tx.status().name()
        );
    }
}
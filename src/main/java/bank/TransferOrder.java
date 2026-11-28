package bank;

import bank.domain.AccountNumber;

// 28.11 этап 1: заявка на перевод с уникальным ID
public record TransferOrder(
        long id,
        AccountNumber from,
        AccountNumber to,
        double amount
) {
}
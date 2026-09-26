package bank;

public record Transaction(
        TransactionType type,
        AccountNumber account,
        double amount,
        TransactionStatus status
) {
}
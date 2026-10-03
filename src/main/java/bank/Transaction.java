package bank;

public record Transaction(
        Long id,
        TransactionType type,
        AccountNumber account,
        double amount,
        TransactionStatus status
) implements Identifiable<Long> {

    @Override
    public Long getId() {
        return id;
    }
}
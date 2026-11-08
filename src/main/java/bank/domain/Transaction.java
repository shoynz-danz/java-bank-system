package bank.domain;

import bank.Identifiable;
import bank.TransactionStatus;
import bank.TransactionType;

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
package bank;

import bank.domain.AccountNumber;

public record AccountSummary(
        AccountNumber number,
        String type,
        double balance
) {
}
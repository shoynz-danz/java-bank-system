package bank;

import bank.domain.BankAccount;

// 14.11 этап 1: DTO для внешнего представления счёта
public record AccountDto(
        String number,
        String owner,
        double balance,
        String type
) {
    public static AccountDto fromDomain(BankAccount account) {
        return new AccountDto(
                account.getId().value(),
                account.getOwner(),
                account.getBalance(),
                account.getClass().getSimpleName()
        );
    }
}
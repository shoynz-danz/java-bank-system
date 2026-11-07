package bank;

// 07.11 этап 6: Factory (паттерн Фабрика для создания счетов)
public class DefaultAccountFactory implements AccountFactory {

    @Override
    public BankAccount create(
            AccountType type,
            AccountNumber number,
            String owner,
            double balance) {

        return switch (type) {
            case DEBIT ->
                    new DebitAccount(number, owner, balance);
            case SAVINGS ->
                    new SavingsAccount(number.value(), owner, balance, 1000);
            case CREDIT ->
                    new CreditAccount(number.value(), owner, balance, 50_000);
        };
    }
}
package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Predicate;

public class CollectionFunctionsTest {

    @Test
    void filterPositiveBalance() {
        // баланс больше нуля
        List<BankAccount> accounts = List.of(
            new DebitAccount("0000000001", "Ivan", 1000),
            new DebitAccount("0000000002", "Petr", 0),
            new DebitAccount("0000000003", "Anna", 0)
        );

        Predicate<BankAccount> positiveBalance =
            account -> account.getBalance() > 0;

        List<BankAccount> result = CollectionFunctions.filter(accounts, positiveBalance);

        assertEquals(1, result.size());
        assertSame(accounts.get(0), result.get(0));
    }

    @Test
    void filterRich() {
        // баланс от 100 000
        List<BankAccount> accounts = List.of(
            new DebitAccount("0000000001", "Ivan", 50_000),
            new DebitAccount("0000000002", "Petr", 100_000),
            new DebitAccount("0000000003", "Anna", 150_000)
        );

        Predicate<BankAccount> rich =
            account -> account.getBalance() >= 100_000;

        List<BankAccount> result = CollectionFunctions.filter(accounts, rich);

        assertEquals(2, result.size());
        assertSame(accounts.get(1), result.get(0));
        assertSame(accounts.get(2), result.get(1));
    }

    @Test
    void filterDebitOnly() {
        // только дебетовые счета
        List<BankAccount> accounts = List.of(
            new DebitAccount("0000000001", "Ivan", 1000),
            new SavingsAccount("0000000002", "Petr", 2000, 100),
            new CreditAccount("0000000003", "Anna", 3000, 5000)
        );

        Predicate<BankAccount> debitOnly = DebitAccount.class::isInstance;

        List<BankAccount> result = CollectionFunctions.filter(accounts, debitOnly);

        assertEquals(1, result.size());
        assertSame(accounts.get(0), result.get(0));
    }
}
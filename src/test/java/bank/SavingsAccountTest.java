package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
Для SavingsAccount:

text
можно снять деньги, если после операции сохраняется минимальный остаток;
нельзя опустить баланс ниже minimumBalance;
при неуспешном снятии баланс не изменяется.
 */

public class SavingsAccountTest {

    @Test
    void withdrawAllowedWhenMinimumBalancePreserved() {
        // Баланс 10 000, неснижаемый мин 1000
        SavingsAccount account = new SavingsAccount("2", "Petr", 10000.0, 1000.0);
        boolean result = account.withdraw(8500.0); // останется 1500 (> 1000)
        assertTrue(result);
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void cannotWithdrawBelowMinimumBalance() {
        // Баланс 10000, минимум 1000
        SavingsAccount account = new SavingsAccount("2", "Petr", 10000.0, 1000.0);
        boolean result = account.withdraw(9500.0); // осталось бы 500 (< 1000)
        assertFalse(result);
        assertEquals(10000.0, account.getBalance());
    }

    @Test
    void failedWithdrawDoesNotChangeBalance() {
        SavingsAccount account = new SavingsAccount("2", "Petr", 1500.0, 1000.0);
        boolean result = account.withdraw(1000.0); // осталось бы 500, отказан
        assertFalse(result);
        assertEquals(1500.0, account.getBalance());
    }
}
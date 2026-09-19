package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
Для CreditAccount:

text
счёт может уйти в отрицательный баланс;
можно использовать кредитный лимит;
нельзя превысить кредитный лимит;
при неуспешном снятии баланс не изменяется.
 */

public class CreditAccountTest {

    @Test
    void balanceCanGoNegative() {
        // Баланс 1 000, кредитный лимит 5 000
        CreditAccount account = new CreditAccount("3", "Anna", 1000.0, 5000.0);
        boolean result = account.withdraw(4000.0);
        assertTrue(result);
        assertEquals(-3000.0, account.getBalance());
    }

    @Test
    void canWithdrawUpToExactCreditLimit() {
        CreditAccount account = new CreditAccount("3", "Anna", 0.0, 5000.0);
        boolean result = account.withdraw(5000.0);
        assertTrue(result);
        assertEquals(-5000.0, account.getBalance());
    }

    @Test
    void cannotExceedCreditLimit() {
        // Баланс уже -3 000 при лимите 5 000
        CreditAccount account = new CreditAccount("3", "Anna", 1000.0, 5000.0);
        account.withdraw(4000.0); // стало -3000

        boolean result = account.withdraw(3000.0); // попытка уйти в -6000
        assertFalse(result);
        assertEquals(-3000.0, account.getBalance());
    }

    @Test
    void failedWithdrawDoesNotChangeBalance() {
        CreditAccount account = new CreditAccount("3", "Anna", 100.0, 1000.0);
        boolean result = account.withdraw(2000.0);
        assertFalse(result);
        assertEquals(100.0, account.getBalance());
    }
}
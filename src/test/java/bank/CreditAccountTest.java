package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreditAccountTest {

    @Test
    void balanceCanGoNegative() {
        CreditAccount account = new CreditAccount("0000000001", "Anna", 1000.0, 5000.0);
        account.withdraw(4000.0);
        assertEquals(-3000.0, account.getBalance());
    }

    @Test
    void canWithdrawUpToExactCreditLimit() {
        CreditAccount account = new CreditAccount("0000000001", "Anna", 0.0, 5000.0);
        account.withdraw(5000.0);
        assertEquals(-5000.0, account.getBalance());
    }

    @Test
    void cannotExceedCreditLimit() {
        CreditAccount account = new CreditAccount("0000000001", "Anna", 1000.0, 5000.0);
        account.withdraw(4000.0);

        assertThrows(InsufficientFundsException.class, () -> account.withdraw(3000.0));
        assertEquals(-3000.0, account.getBalance());
    }

    @Test
    void failedWithdrawDoesNotChangeBalance() {
        CreditAccount account = new CreditAccount("0000000001", "Anna", 100.0, 1000.0);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(2000.0));
        assertEquals(100.0, account.getBalance());
    }
}
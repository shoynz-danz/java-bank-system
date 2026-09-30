package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SavingsAccountTest {

    @Test
    void withdrawAllowedWhenMinimumBalancePreserved() {
        SavingsAccount account = new SavingsAccount("0000000001", "Petr", 10000.0, 1000.0);
        account.withdraw(8500.0);
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void cannotWithdrawBelowMinimumBalance() {
        SavingsAccount account = new SavingsAccount("0000000001", "Petr", 10000.0, 1000.0);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(9500.0));
        assertEquals(10000.0, account.getBalance());
    }

    @Test
    void failedWithdrawDoesNotChangeBalance() {
        SavingsAccount account = new SavingsAccount("0000000001", "Petr", 1500.0, 1000.0);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(1000.0));
        assertEquals(1500.0, account.getBalance());
    }
}
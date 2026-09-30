package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DebitAccountTest {

    @Test
    void initialBalanceIsPreserved() {
        DebitAccount account = new DebitAccount("0000000001", "Ivan", 1000.0);
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void depositIncreasesBalance() {
        DebitAccount account = new DebitAccount("0000000001", "Ivan", 1000.0);
        account.deposit(500.0);
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void negativeDepositThrowsException() {
        DebitAccount account = new DebitAccount("0000000001", "Лёха", 1000.0);
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-100.0));
    }

    @Test
    void zeroDepositThrowsException() {
        DebitAccount account = new DebitAccount("0000000001", "Лёха", 1000.0);
        assertThrows(IllegalArgumentException.class, () -> account.deposit(0.0));
    }

    @Test
    void withdrawDecreasesBalance() {
        DebitAccount account = new DebitAccount("0000000001", "Ivan", 10000.0);
        account.withdraw(8000.0);
        assertEquals(2000.0, account.getBalance());
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        DebitAccount account = new DebitAccount("0000000001", "Ivan", 2000.0);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(3000.0));
        assertEquals(2000.0, account.getBalance());
    }

    @Test
    void zeroWithdrawIsForbidden() {
        DebitAccount account = new DebitAccount("0000000001", "Ivan", 1000.0);
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(0.0));
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void negativeWithdrawIsForbidden() {
        DebitAccount account = new DebitAccount("0000000001", "Ivan", 1000.0);
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(-500.0));
        assertEquals(1000.0, account.getBalance());
    }
}
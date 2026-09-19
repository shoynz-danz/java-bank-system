package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
Для DebitAccount напишите тесты минимум для следующих случаев:

text
начальный баланс сохраняется;
deposit увеличивает баланс;
нулевой deposit не изменяет баланс;
отрицательный deposit не изменяет баланс;
withdraw уменьшает баланс;
нельзя снять больше остатка;
нулевое снятие запрещено;
отрицательное снятие запрещено.

 */

public class DebitAccountTest {

    @Test
    void initialBalanceIsPreserved() {
        DebitAccount account = new DebitAccount("1", "Ivan", 1000.0);
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void depositIncreasesBalance() {
        DebitAccount account = new DebitAccount("1", "Ivan", 1000.0);
        account.deposit(500.0);
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void zeroDepositDoesNotChangeBalance() {
        DebitAccount account = new DebitAccount("1", "Ivan", 1000.0);
        account.deposit(0.0);
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void negativeDepositDoesNotChangeBalance() {
        DebitAccount account = new DebitAccount("1", "Ivan", 1000.0);
        account.deposit(-200.0);
        assertEquals(1000.0, account.getBalance());
    }

    // withdraw уменьшает баланс
    @Test
    void withdrawDecreasesBalance() {
        DebitAccount account = new DebitAccount("1", "Ivan", 10000.0);
        boolean result = account.withdraw(8000.0);
        assertTrue(result);
        assertEquals(2000.0, account.getBalance());
    }

    // Нельзя снять больше остатка
    @Test
    void cannotWithdrawMoreThanBalance() {
        DebitAccount account = new DebitAccount("1", "Ivan", 2000.0);
        boolean result = account.withdraw(3000.0);
        assertFalse(result);
        assertEquals(2000.0, account.getBalance());
    }

    @Test
    void zeroWithdrawIsForbidden() {
        DebitAccount account = new DebitAccount("1", "Ivan", 1000.0);
        boolean result = account.withdraw(0.0);
        assertFalse(result);
        assertEquals(1000.0, account.getBalance());
    }

    // Отрицательное снятие запрещено
    @Test
    void negativeWithdrawIsForbidden() {
        DebitAccount account = new DebitAccount("1", "Ivan", 1000.0);
        boolean result = account.withdraw(-500.0);
        assertFalse(result);
        assertEquals(1000.0, account.getBalance());
    }
}
package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BankAccountTest {

    @Test
    void accountsWithSameNumberAreEqual() {
        BankAccount a = new DebitAccount("001", "Лёха", 1000);
        BankAccount b = new CreditAccount("001", "Лёха на кредитке", 5000, 2000);
        assertEquals(a, b);
    }

    @Test
    void accountsWithDifferentNumbersAreNotEqual() {
        BankAccount a = new DebitAccount("001", "Лёха", 1000);
        BankAccount b = new DebitAccount("002", "Другой Лёха", 1000);
        assertNotEquals(a, b);
    }

    @Test
    void accountEqualsItself() {
        BankAccount a = new DebitAccount("001", "Лёха", 1000);
        assertEquals(a, a);
    }

    @Test
    void accountDoesNotEqualNull() {
        BankAccount a = new DebitAccount("001", "Лёха", 1000);
        assertNotEquals(null, a);
    }

    @Test
    void equalAccountsHaveSameHashCode() {
        BankAccount a = new DebitAccount("001", "Лёха", 1000);
        BankAccount b = new SavingsAccount("001", "Лёха с копилкой", 3000, 500);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
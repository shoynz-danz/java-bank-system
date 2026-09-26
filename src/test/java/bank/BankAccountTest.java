package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BankAccountTest {

    @Test
    void accountsWithSameNumberAreEqual() {
        BankAccount a = new DebitAccount("0000000001", "Лёха", 1000);
        BankAccount b = new CreditAccount("0000000001", "Лёха на кредитке", 5000, 2000);
        assertEquals(a, b);
    }

    @Test
    void accountsWithDifferentNumbersAreNotEqual() {
        BankAccount a = new DebitAccount("0000000001", "Лёха", 1000);
        BankAccount b = new DebitAccount("0000000002", "Другой Лёха", 1000);
        assertNotEquals(a, b);
    }

    @Test
    void accountEqualsItself() {
        BankAccount a = new DebitAccount("0000000001", "Лёха", 1000);
        assertEquals(a, a);
    }

    @Test
    void accountDoesNotEqualNull() {
        BankAccount a = new DebitAccount("0000000001", "Лёха", 1000);
        assertNotEquals(null, a);
    }

    @Test
    void equalAccountsHaveSameHashCode() {
        BankAccount a = new DebitAccount("0000000001", "Лёха", 1000);
        BankAccount b = new SavingsAccount("0000000001", "Лёха с копилкой", 3000, 500);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
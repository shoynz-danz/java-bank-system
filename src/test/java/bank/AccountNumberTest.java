package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountNumberTest {

    @Test
    void validNumberIsCreated() {
        AccountNumber number = new AccountNumber("1234567890");
        assertEquals("1234567890", number.value());
    }

    @Test
    void shortNumberIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("123"));
    }

    @Test
    void nullIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber(null));
    }

    @Test
    void emptyStringIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber(""));
    }

    @Test
    void nineDigitsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("123456789"));
    }

    @Test
    void elevenDigitsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("12345678901"));
    }

    @Test
    void lettersInsideNumberAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("12345abcde"));
    }
}
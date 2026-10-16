package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BlockedAccountRegistryTest {

    @Test
    void blockingSameAccountTwiceDoesNotCreateDuplicate() {
        // тест из задания
        BlockedAccountRegistry registry = new BlockedAccountRegistry();
        AccountNumber number = new AccountNumber("0000000001");
        registry.block(number);
        registry.block(number);
        assertEquals(1, registry.size());
    }

    @Test
    void blockedAccountIsBlocked() {
        // подготовка
        BlockedAccountRegistry registry = new BlockedAccountRegistry();
        AccountNumber number = new AccountNumber("0000000001");

        // действие
        registry.block(number);

        // проверка
        assertTrue(registry.isBlocked(number));
    }

    @Test
    void unblockRemovesNumber() {
        // подготовка
        BlockedAccountRegistry registry = new BlockedAccountRegistry();
        AccountNumber number = new AccountNumber("0000000001");
        registry.block(number);

        // действие
        registry.unblock(number);

        // проверка
        assertFalse(registry.isBlocked(number));
        assertEquals(0, registry.size());
    }

    @Test
    void unknownNumberIsNotBlocked() {
        BlockedAccountRegistry registry = new BlockedAccountRegistry();

        assertFalse(registry.isBlocked(new AccountNumber("0000000009")));
    }
}

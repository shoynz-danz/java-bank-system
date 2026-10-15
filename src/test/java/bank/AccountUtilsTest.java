package bank;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AccountUtilsTest {

    @Test
    void richestKeepsDebitType() {
        // подготовка
        List<DebitAccount> values = new ArrayList<>();
        values.add(new DebitAccount("0000000001", "Ivan", 1000));
        values.add(new DebitAccount("0000000002", "Petr", 5000));
        values.add(new DebitAccount("0000000003", "Anna", 3000));

        // действие тип на выходе тот же, приводить не надо
        DebitAccount richest = AccountUtils.richest(values);

        // проверка
        assertEquals("0000000002", richest.getNumber().value());
        assertEquals(5000, richest.getBalance());
    }

    @Test
    void richestOfSingleAccount() {
        List<DebitAccount> values = new ArrayList<>();
        values.add(new DebitAccount("0000000001", "Ivan", 1000));

        assertEquals("0000000001", AccountUtils.richest(values).getNumber().value());
    }

    @Test
    void richestWithSameBalanceReturnsFirst() {
        // если балансы равные, побеждает первый, потому что строго >
        List<DebitAccount> values = new ArrayList<>();
        values.add(new DebitAccount("0000000001", "Ivan", 2000));
        values.add(new DebitAccount("0000000002", "Petr", 2000));

        assertEquals("0000000001", AccountUtils.richest(values).getNumber().value());
    }

    @Test
    void richestOfEmptyListThrowsException() {
        List<DebitAccount> values = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> AccountUtils.richest(values));
    }
}

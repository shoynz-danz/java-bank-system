package bank;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BankReportsTest {

    @Test
    void totalBalanceOfDebitAccounts() {
        // подготовка
        List<DebitAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount("0000000001", "Ivan", 1000));
        accounts.add(new DebitAccount("0000000002", "Petr", 2000));

        // проверка метод берёт и List<DebitAccount>
        assertEquals(3000, BankReports.totalBalance(accounts));
    }

    @Test
    void totalBalanceOfMixedAccounts() {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount("0000000001", "Ivan", 1000));
        accounts.add(new SavingsAccount("0000000002", "Petr", 5000, 1000));

        assertEquals(6000, BankReports.totalBalance(accounts));
    }

    @Test
    void totalBalanceOfEmptyList() {
        List<CreditAccount> accounts = new ArrayList<>();

        assertEquals(0, BankReports.totalBalance(accounts));
    }

    @Test
    void addDemoAccountsToDebitList() {
        // подготовка пустой List<DebitAccount>
        List<DebitAccount> target = new ArrayList<>();

        // действие
        BankReports.addDemoDebitAccounts(target);

        // проверка добавилось два счёта
        assertEquals(2, target.size());
        assertEquals(3000, BankReports.totalBalance(target));
    }

    @Test
    void addDemoAccountsToBankAccountList() {
        // метод берёт и List<BankAccount>, дебетовый туда влезет
        List<BankAccount> target = new ArrayList<>();

        BankReports.addDemoDebitAccounts(target);

        assertEquals(2, target.size());
        assertEquals(3000, BankReports.totalBalance(target));
    }

    @Test
    void addDemoAccountsToObjectList() {
        // а сюда влезет вообще всё, так что дебетовые тоже можно
        List<Object> target = new ArrayList<>();

        BankReports.addDemoDebitAccounts(target);

        assertEquals(2, target.size());
        // читать отсюда можно только как Object, поэтому проверяем так
        Object first = target.get(0);
        assertTrue(first instanceof DebitAccount);
    }
}

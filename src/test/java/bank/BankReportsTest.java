package bank;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Test
    void copyDebitAccountsToBankAccountList() {
        // подготовка
        List<DebitAccount> source = new ArrayList<>();
        DebitAccount first = new DebitAccount("0000000001", "Ivan", 1000);
        DebitAccount second = new DebitAccount("0000000002", "Petr", 2000);
        source.add(first);
        source.add(second);
        List<BankAccount> target = new ArrayList<>();

        // действие
        BankReports.copy(source, target);

        // проверка всё переехало и порядок тот же
        assertEquals(2, target.size());
        assertSame(first, target.get(0));
        assertSame(second, target.get(1));
    }

    @Test
    void copyDebitAccountsToObjectList() {
        List<DebitAccount> source = new ArrayList<>();
        source.add(new DebitAccount("0000000001", "Ivan", 1000));
        source.add(new DebitAccount("0000000002", "Petr", 2000));
        List<Object> target = new ArrayList<>();

        BankReports.copy(source, target);

        assertEquals(2, target.size());
        assertTrue(target.get(0) instanceof DebitAccount);
        assertTrue(target.get(1) instanceof DebitAccount);
    }

    @Test
    void copyBankAccountsToBankAccountList() {
        List<BankAccount> source = new ArrayList<>();
        source.add(new DebitAccount("0000000001", "Ivan", 1000));
        source.add(new SavingsAccount("0000000002", "Petr", 5000, 1000));
        List<BankAccount> target = new ArrayList<>();

        BankReports.copy(source, target);

        assertEquals(2, target.size());
        assertEquals(6000, BankReports.totalBalance(target));
    }

    @Test
    void copyKeepsOrder() {
        // порядок проверяем по номерам счетов
        List<DebitAccount> source = new ArrayList<>();
        source.add(new DebitAccount("0000000001", "Ivan", 100));
        source.add(new DebitAccount("0000000002", "Petr", 200));
        source.add(new DebitAccount("0000000003", "Anna", 300));
        List<BankAccount> target = new ArrayList<>();

        BankReports.copy(source, target);

        assertEquals("0000000001", target.get(0).getNumber().value());
        assertEquals("0000000002", target.get(1).getNumber().value());
        assertEquals("0000000003", target.get(2).getNumber().value());
    }

    private Transaction tx(long id, String number, double amount) {
        // помощник чтобы не писать одно и то же
        return new Transaction(
                id,
                TransactionType.TRANSFER,
                new AccountNumber(number),
                amount,
                TransactionStatus.SUCCESS
        );
    }

    @Test
    void countByAccountEmpty() {
        // пустой список — пустая мапа
        assertTrue(BankReports.countByAccount(new ArrayList<>()).isEmpty());
    }

    @Test
    void countByAccountSingle() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(tx(1L, "0000000001", 1000));

        Map<AccountNumber, Integer> result = BankReports.countByAccount(transactions);

        assertEquals(1, result.size());
        assertEquals(1, result.get(new AccountNumber("0000000001")));
    }

    @Test
    void countByAccountMultiple() {
        // два счёта, у первого две операции
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(tx(1L, "0000000001", 1000));
        transactions.add(tx(2L, "0000000002", 500));
        transactions.add(tx(3L, "0000000001", 2000));

        Map<AccountNumber, Integer> result = BankReports.countByAccount(transactions);

        assertEquals(2, result.size());
        assertEquals(2, result.get(new AccountNumber("0000000001")));
        assertEquals(1, result.get(new AccountNumber("0000000002")));
    }

    @Test
    void countByAccountSameValueDifferentObjects() {
        // разные объекты AccountNumber, но value одинаковый — одна запись
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(tx(1L, "0000000001", 1000));
        transactions.add(tx(2L, "0000000001", 2000));

        Map<AccountNumber, Integer> result = BankReports.countByAccount(transactions);

        assertEquals(1, result.size());
        assertEquals(2, result.get(new AccountNumber("0000000001")));
    }

    @Test
    void totalAmountByAccount() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(tx(1L, "0000000001", 1000));
        transactions.add(tx(2L, "0000000002", 500));
        transactions.add(tx(3L, "0000000001", 2000));

        Map<AccountNumber, Double> result = BankReports.totalAmountByAccount(transactions);

        assertEquals(2, result.size());
        assertEquals(3000.0, result.get(new AccountNumber("0000000001")));
        assertEquals(500.0, result.get(new AccountNumber("0000000002")));
    }
}

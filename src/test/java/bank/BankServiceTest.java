package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {

    private BankService service() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        TransferService transfers = new TransferService(new NoCommission(), new FakeNotificationService());
        return new BankService(repo, transfers);
    }

    @Test
    void openThenFind() {
        BankService service = service();
        BankAccount account = new DebitAccount("0000000001", "Ivan", 1000);

        service.openAccount(account);

        assertSame(account, service.findAccount(new AccountNumber("0000000001")));
    }

    @Test
    void saveSameIdTwiceReplaces() {
        BankService service = service();
        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));

        BankAccount updated = new DebitAccount("0000000001", "Petr", 5000);
        service.openAccount(updated);

        BankAccount found = service.findAccount(new AccountNumber("0000000001"));
        assertSame(updated, found);
    }

    @Test
    void closeDeletesAccount() {
        BankService service = service();
        AccountNumber number = new AccountNumber("0000000001");
        service.openAccount(new DebitAccount("0000000001", "Ivan", 1000));

        boolean closed = service.closeAccount(number);

        assertTrue(closed);
        assertNull(service.findAccount(number));
    }
}

package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
Минимальный набор:

text
успешный перевод изменяет оба баланса;
неуспешный перевод не изменяет ни один баланс;
нельзя переводить отрицательную сумму;
нельзя переводить нулевую сумму;
нельзя переводить деньги самому себе;
комиссия списывается с отправителя;
получатель получает ровно сумму перевода;
перевод не выполняется, если денег недостаточно с учётом комиссии.
 */

public class TransferServiceTest {

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new ConsoleNotificationService();
    }

    @Test
    void successfulTransferChangesBothBalances() {
        DebitAccount from = new DebitAccount("1", "Ivan", 10000.0);
        DebitAccount to = new DebitAccount("2", "Petr", 2000.0);
        TransferService service = new TransferService(new NoCommission(), notificationService);

        boolean result = service.transfer(from, to, 3000.0);

        assertTrue(result);
        assertEquals(7000.0, from.getBalance());
        assertEquals(5000.0, to.getBalance());
    }

    @Test
    void failedTransferDoesNotChangeAnyBalance() {
        DebitAccount from = new DebitAccount("1", "Ivan", 1000.0);
        DebitAccount to = new DebitAccount("2", "Petr", 2000.0);
        TransferService service = new TransferService(new NoCommission(), notificationService);

        boolean result = service.transfer(from, to, 3000.0); // Денег не хватает

        assertFalse(result);
        assertEquals(1000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void cannotTransferZeroOrNegativeAmount() {
        DebitAccount from = new DebitAccount("1", "Ivan", 5000.0);
        DebitAccount to = new DebitAccount("2", "Petr", 2000.0);
        TransferService service = new TransferService(new NoCommission(), notificationService);

        assertFalse(service.transfer(from, to, 0.0));
        assertFalse(service.transfer(from, to, -500.0));
        assertEquals(5000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void cannotTransferToSameAccount() {
        DebitAccount account = new DebitAccount("1", "Ivan", 5000.0);
        TransferService service = new TransferService(new NoCommission(), notificationService);

        boolean result = service.transfer(account, account, 1000.0);

        assertFalse(result);
        assertEquals(5000.0, account.getBalance());
    }

    @Test
    void commissionDeductedFromSenderAndRecipientGetsExactAmount() {
        DebitAccount from = new DebitAccount("1", "Ivan", 11000.0);
        DebitAccount to = new DebitAccount("2", "Petr", 0.0);
        // Комиссия 1 процентв
        TransferService service = new TransferService(new PercentCommission(1.0), notificationService);

        boolean result = service.transfer(from, to, 10000.0);

        assertTrue(result);
        // Списано 10000 перевода + 100 комиссии. Баланс 900
        assertEquals(900.0, from.getBalance());
        // дошли чистые 10 000
        assertEquals(10000.0, to.getBalance());
    }

    @Test
    void transferFailsIfInsufficientFundsWithCommission() {
        DebitAccount from = new DebitAccount("1", "Ivan", 10050.0);
        DebitAccount to = new DebitAccount("2", "Petr", 0.0);
        // Для перевода 10 000 с комиссией 1% нужно 10 100, а у клиента только 10 050
        TransferService service = new TransferService(new PercentCommission(1.0), notificationService);

        boolean result = service.transfer(from, to, 10000.0);

        assertFalse(result);
        assertEquals(10050.0, from.getBalance());
        assertEquals(0.0, to.getBalance());
    }

    // Проверка полиморфизма: переводы между разными типами счетов
    /*
    Проверьте переводы между разными типами счетов:
    text
    DebitAccount → DebitAccount
    DebitAccount → SavingsAccount
    CreditAccount → DebitAccount
    SavingsAccount → DebitAccount
     */
    @Test
    void transfersBetweenDifferentAccountTypes() {
        DebitAccount debit = new DebitAccount("1", "Ivan", 5000.0);
        SavingsAccount savings = new SavingsAccount("2", "Petr", 2000.0, 1000.0);
        CreditAccount credit = new CreditAccount("3", "Anna", 0.0, 5000.0);

        TransferService service = new TransferService(new NoCommission(), notificationService);

        assertTrue(service.transfer(debit, savings, 1000.0));
        assertEquals(4000.0, debit.getBalance());
        assertEquals(3000.0, savings.getBalance());

        assertTrue(service.transfer(credit, debit, 2000.0));
        assertEquals(-2000.0, credit.getBalance());
        assertEquals(6000.0, debit.getBalance());

        assertTrue(service.transfer(savings, debit, 1000.0));
        assertEquals(2000.0, savings.getBalance());
        assertEquals(7000.0, debit.getBalance());
    }

    // 9 ЭТАП: Проверка уведомлений через Fake-объект

    @Test
    void notificationSentOnSuccessfulTransfer() {
        // ПОДГОТОВКА
        DebitAccount from = new DebitAccount("1", "Ivan", 5000.0);
        DebitAccount to = new DebitAccount("2", "Petr", 1000.0);
        FakeNotificationService fakeNotifier = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), fakeNotifier);

        // Действие
        service.transfer(from, to, 3000.0);

        // проверка: отправлено ровно 1 уведомление и с правильным текстом
        assertEquals(1, fakeNotifier.getNotificationCount());
        assertEquals("Transfer 3000.0 completed", fakeNotifier.getLastMessage());
    }

    @Test
    void notificationNotSentOnFailedTransfer() {
        // подготовк
        DebitAccount from = new DebitAccount("1", "Ivan", 1000.0);
        DebitAccount to = new DebitAccount("2", "Petr", 1000.0);
        FakeNotificationService fakeNotifier = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), fakeNotifier);

        // действие переводим больше, чем есть
        service.transfer(from, to, 5000.0);

        // провеерка уведомлений быть не должно
        assertEquals(0, fakeNotifier.getNotificationCount());
        assertNull(fakeNotifier.getLastMessage());
    }
}
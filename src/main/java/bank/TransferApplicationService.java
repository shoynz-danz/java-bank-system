package bank;

/*
07.11 этап 2: прикладной сервис переводов по номерам счетов (SRP).
не отвечает за отчёты и аналитику.
 */
public class TransferApplicationService {

    private final Repository<AccountNumber, BankAccount> accounts;
    private final TransferService transferService;

    public TransferApplicationService(
            Repository<AccountNumber, BankAccount> accounts,
            TransferService transferService) {
        if (accounts == null) {
            throw new IllegalArgumentException("счета null");
        }
        if (transferService == null) {
            throw new IllegalArgumentException("переводы null");
        }
        this.accounts = accounts;
        this.transferService = transferService;
    }

    public void transfer(AccountNumber fromNumber, AccountNumber toNumber, double amount) {
        BankAccount from = accounts.findById(fromNumber);
        if (from == null) {
            throw new EntityNotFoundException("счёт не найден");
        }
        BankAccount to = accounts.findById(toNumber);
        if (to == null) {
            throw new EntityNotFoundException("счёт не найден");
        }
        transferService.transfer(from, to, amount);
    }
}
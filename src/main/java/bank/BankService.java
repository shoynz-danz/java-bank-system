package bank;

/*
17.10 этап 1: сервис владеет счетами.
клиент ходит сюда, а не в Repository напрямую.
 */
public class BankService {

    private final Repository<AccountNumber, BankAccount> accounts;
    private final TransferService transferService;

    public BankService(
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

    public void openAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("счёт null");
        }
        accounts.save(account);
    }

    public BankAccount findAccount(AccountNumber number) {
        return accounts.findById(number);
    }

    public boolean closeAccount(AccountNumber number) {
        return accounts.deleteById(number);
    }

    // 17.10 этап 2: перевод по номерам, сами счета достаём внутри
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

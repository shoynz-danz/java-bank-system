package bank;

import bank.domain.AccountNumber;

// 21.11 этап 1: клиент, выполняющий много переводов в отдельном потоке
public class ClientWorker implements Runnable {

    private final BankService bank;
    private final AccountNumber from;
    private final AccountNumber to;
    private final int operations;

    public ClientWorker(BankService bank, String from, String to, int operations) {
        this.bank = bank;
        this.from = new AccountNumber(from);
        this.to = new AccountNumber(to);
        this.operations = operations;
    }

    @Override
    public void run() {
        for (int i = 0; i < operations; i++) {
            try {
                bank.transfer(from, to, 10.0);
            } catch (RuntimeException e) {
                // ошибка одной операции не завершает поток
            }
        }
    }
}
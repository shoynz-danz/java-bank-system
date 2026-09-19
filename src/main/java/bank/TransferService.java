package bank;

public class TransferService {

    public boolean transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0 || from == null || to == null || from == to) {
            return false;
        }
        boolean withdrawSuccess = from.withdraw(amount);

        if (!withdrawSuccess) {
            return false;
        }

        to.deposit(amount);
        return true;
    }
}

/*
Правила перевода

1. Сумма перевода должна быть больше нуля.
2. Нельзя переводить деньги со счёта на тот же самый счёт.
3. Списание выполняется по правилам конкретного типа счёта.
4. Если списание невозможно, перевод считается неуспешным.
5. При неуспешном переводе баланс получателя не должен измениться.
6. При успешном переводе деньги списываются со счёта отправителя и зачисляются получателю.
 */
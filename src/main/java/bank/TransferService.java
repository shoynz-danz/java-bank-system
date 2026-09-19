package bank;

public class TransferService {
    private final CommissionPolicy commissionPolicy;

    // Теперь объект CommissionPolicy должен передаваться в него через конструктор:
    public TransferService(CommissionPolicy commissionPolicy) {
        this.commissionPolicy = commissionPolicy;
    }

    public boolean transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0 || from == null || to == null || from == to) {
            return false;
        }

        double commission = commissionPolicy.calculate(amount);
        double totalToWithdraw = amount + commission;

        boolean withdrawSuccess = from.withdraw(totalToWithdraw);
        if (!withdrawSuccess) {
            return false;
        }

        to.deposit(amount);
        return true;
    }
}
// Изменённый код, этап 4
// Внедряем политику комиссии через конструктор
// 1. Считаем комиссию и общую сумму к списанию
// 2. Списываем с отправителя СУММУ С КОМИССИЕЙ
// 3. Получателю начисляем сумму перевода

package bank;

import java.util.List;

/*
03.10 этап 4: wildcard ? extends - producer.
метод только читает счета, поэтому принимает любые:
List<BankAccount>, List<DebitAccount>, List<SavingsAccount> и т.д.
Producer Extends: раз список нам что-то выдаёт, пишем extends.
 */
public class BankReports {

    public static double totalBalance(List<? extends BankAccount> accounts) {
        double total = 0;
        for (BankAccount account : accounts) {
            total += account.getBalance();
        }
        return total;
        // а вот так нельзя: accounts.add(new DebitAccount("0000000001", "Ivan", 100));
        // компилятор не даёт добавлять, потому что настоящий список
        // может оказаться List<SavingsAccount>, куда дебетовый класть нельзя
    }
}

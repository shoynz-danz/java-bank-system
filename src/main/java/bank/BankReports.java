package bank;

import java.util.List;

/*
03.10 этап 4: wildcard ? extends - producer.
метод только читает счета, поэтому принимает любые:
List<BankAccount>, List<DebitAccount>, List<SavingsAccount> и т.д.
Producer Extends: раз список нам что-то выдаёт, пишем extends.

03.10 этап 5: wildcard ? super - consumer.
метод только кладёт дебетовые счета, поэтому принимает списки
куда дебетовый точно влезет: List<DebitAccount>, List<BankAccount>, List<Object>.
Consumer Super: раз список наши значения принимает, пишем super.
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

    public static void addDemoDebitAccounts(List<? super DebitAccount> target) {
        target.add(new DebitAccount("0000000001", "Ivan", 1000));
        target.add(new DebitAccount("0000000002", "Petr", 2000));
        // а вот читать отсюда как BankAccount нельзя:
        // BankAccount first = target.get(0);
        // компилятор гарантирует только Object, потому что настоящий список
        // может оказаться List<Object>, где лежит что угодно
    }
}

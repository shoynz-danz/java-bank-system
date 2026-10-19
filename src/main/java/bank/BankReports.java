package bank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
03.10 этап 4: wildcard ? extends - producer.
метод только читает счета, поэтому принимает любые:
List<BankAccount>, List<DebitAccount>, List<SavingsAccount> и т.д.
Producer Extends: раз список нам что-то выдаёт, пишем extends.

03.10 этап 5: wildcard ? super - consumer.
метод только кладёт дебетовые счета, поэтому принимает списки
куда дебетовый точно влезет: List<DebitAccount>, List<BankAccount>, List<Object>.
Consumer Super: раз список наши значения принимает, пишем super.

03.10 этап 6: универсальный copy, тут оба правила сразу.
<T> - тип придумывает сам компилятор, когда видит вызов.
source только отдаёт T, значит producer, пишем ? extends T.
target только принимает T, значит consumer, пишем ? super T.

17.10 этап 5: статистика на Map.
идём по транзакциям один раз и складываем в мапу:
сколько штук и на какую сумму по каждому номеру.
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

    public static <T> void copy(List<? extends T> source, List<? super T> target) {
        for (T item : source) {
            target.add(item);
        }
    }

    public static Map<AccountNumber, Integer> countByAccount(List<Transaction> transactions) {
        Map<AccountNumber, Integer> result = new HashMap<>();
        for (Transaction tx : transactions) {
            AccountNumber number = tx.account();
            int old = result.getOrDefault(number, 0);
            result.put(number, old + 1);
        }
        return result;
    }

    public static Map<AccountNumber, Double> totalAmountByAccount(List<Transaction> transactions) {
        Map<AccountNumber, Double> result = new HashMap<>();
        for (Transaction tx : transactions) {
            AccountNumber number = tx.account();
            double old = result.getOrDefault(number, 0.0);
            result.put(number, old + tx.amount());
        }
        return result;
    }
}

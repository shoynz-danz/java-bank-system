package bank;

import java.util.Comparator;
import java.util.List;

/*
03.10 этап 7: метод работает только со счетами.
<T extends BankAccount> значит T это счёт или его наследник.
фишка в том что T связывает вход и выход: подали List<DebitAccount>,
получили назад именно DebitAccount, а не просто BankAccount.
 */
public final class AccountUtils {

    private AccountUtils() {
    }

    // 31.10 этап 9: самый богатый счёт через stream().max()
    public static <T extends BankAccount> T richest(List<T> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            throw new IllegalArgumentException("Список счетов пустой");
        }
        return accounts.stream()
                .max(Comparator.comparingDouble(BankAccount::getBalance))
                .orElseThrow();
    }
}
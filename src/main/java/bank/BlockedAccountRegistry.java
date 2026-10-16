package bank;

import java.util.HashSet;
import java.util.Set;

/*
10.10 этап 2: реестр заблокированных счетов.
храним только номера, Set сам не даёт положить дубликат.
 */
public class BlockedAccountRegistry {

    private final Set<AccountNumber> blocked = new HashSet<>();

    public void block(AccountNumber number) {
        blocked.add(number);
    }

    public void unblock(AccountNumber number) {
        blocked.remove(number);
    }

    public boolean isBlocked(AccountNumber number) {
        return blocked.contains(number);
    }

    public int size() {
        return blocked.size();
    }
}

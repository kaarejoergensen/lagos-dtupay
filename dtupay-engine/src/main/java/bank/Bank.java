package bank;

import models.Account;
import models.AccountInfo;
import models.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface Bank {
    Optional<String> createAccountWithBalance(User user, BigDecimal balance);

    Optional<Account> getAccount(String id);

    Optional<Account> getAccountByCprNumber(String cprNumber);

    Optional<List<AccountInfo>> getAccounts();

    boolean retireAccount(String id);

    boolean transferMoneyFromTo(String fromAccountId, String toAccountId, BigDecimal amount, String description);
}

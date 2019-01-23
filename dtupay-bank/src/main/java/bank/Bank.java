package bank;

import bank.dtu.ws.fastmoney.exceptions.BankServiceException_Exception;
import models.Account;
import models.AccountInfo;
import models.User;

import java.math.BigDecimal;
import java.util.List;
/**
 * @author Kåre
 */
public interface Bank {
    String createAccountWithBalance(User user, BigDecimal balance) throws BankServiceException_Exception;

    Account getAccount(String id) throws BankServiceException_Exception;

    Account getAccountByCprNumber(String cprNumber) throws BankServiceException_Exception;

    List<AccountInfo> getAccounts();

    void retireAccount(String id) throws BankServiceException_Exception;

    void transferMoneyFromTo(String fromAccountId, String toAccountId, BigDecimal amount, String description) throws BankServiceException_Exception;

    Boolean accountExists(String cpr);

}

import bank.Bank;
import bank.BankSOAP;
import bank.dtu.ws.fastmoney.exceptions.BankServiceException_Exception;
import models.Account;
import models.AccountInfo;
import models.Transaction;
import models.User;
import org.junit.After;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;


public class BankTest {
    private Bank bank = new BankSOAP();
    private List<String> createdAccounts = new ArrayList<>();

    @Test
    public void createAccount() throws BankServiceException_Exception {
        User user = new User("995566-2233","FirstName","LastName");
        BigDecimal balance = new BigDecimal(1000);

        String accountId = this.bank.createAccountWithBalance(user, balance);
        assertThat(accountId, is(notNullValue()));
        assertThat(accountId, is(not("")));
        this.createdAccounts.add(accountId);
    }

    @Test
    public void getAccount() throws BankServiceException_Exception {
        User user = new User("995566-2233","FirstName","LastName");
        BigDecimal balance = new BigDecimal(1000);

        String accountId = this.bank.createAccountWithBalance(user, balance);
        assertThat(accountId, is(notNullValue()));
        assertThat(accountId, is(not("")));
        this.createdAccounts.add(accountId);

        Account account = this.bank.getAccount(accountId);
        assertThat(account, is(notNullValue()));
        assertThat(account.getId(), is(accountId));
        assertThat(account.getBalance(), is(balance));
        assertThat(account.getUser(), is(user));
        assertThat(account.getTransactions(), is(Collections.emptyList()));
    }

    @Test
    public void getAccountByCPR() throws BankServiceException_Exception {
        User user = new User("995566-2233","FirstName","LastName");
        BigDecimal balance = new BigDecimal(1000);

        String accountId = this.bank.createAccountWithBalance(user, balance);
        assertThat(accountId, is(notNullValue()));
        assertThat(accountId, is(not("")));
        this.createdAccounts.add(accountId);

        Account account = this.bank.getAccountByCprNumber(user.getCprNumber());
        assertThat(account, is(notNullValue()));
        assertThat(account.getId(), is(accountId));
        assertThat(account.getBalance(), is(balance));
        assertThat(account.getUser(), is(user));
        assertThat(account.getTransactions(), is(Collections.emptyList()));
    }

    @Test
    public void getAccounts() throws BankServiceException_Exception {
        User user = new User("995566-2233","FirstName","LastName");
        BigDecimal balance = new BigDecimal(1000);

        String accountId = this.bank.createAccountWithBalance(user, balance);
        assertThat(accountId, is(notNullValue()));
        assertThat(accountId, is(not("")));
        this.createdAccounts.add(accountId);

        List<AccountInfo> accountInfos = this.bank.getAccounts();
        assertThat(accountInfos, is(notNullValue()));
        assertThat(accountInfos.isEmpty(), is(false));

        AccountInfo accountInfo = accountInfos.stream()
                .filter(ai -> ai.getAccountId().equals(accountId))
                .findFirst().orElse(null);
        assertThat(accountInfo, is(notNullValue()));
        assertThat(accountInfo.getUser(), is(user));
    }

    @Test(expected = BankServiceException_Exception.class)
    public void retireAccount() throws BankServiceException_Exception {
        User user = new User("995566-2233","FirstName","LastName");
        BigDecimal balance = new BigDecimal(1000);

        String accountId = this.bank.createAccountWithBalance(user, balance);
        assertThat(accountId, is(notNullValue()));
        assertThat(accountId, is(not("")));

        this.bank.retireAccount(accountId);
        this.bank.getAccount(accountId);
    }

    @Test
    public void transferMoneyFromTo() throws BankServiceException_Exception {
        User user1 = new User("995566-2233","FirstName","LastName");
        User user2 = new User("662288-5522","FirstName","LastName");
        BigDecimal balance = new BigDecimal(1000);

        String accountId1 = this.bank.createAccountWithBalance(user1, balance);
        String accountId2 = this.bank.createAccountWithBalance(user2, balance);

        assertThat(accountId1, is(notNullValue()));
        assertThat(accountId1, is(not("")));
        this.createdAccounts.add(accountId1);
        assertThat(accountId2, is(notNullValue()));
        assertThat(accountId2, is(not("")));
        this.createdAccounts.add(accountId2);

        Account account1 = this.bank.getAccount(accountId1);
        Account account2 = this.bank.getAccount(accountId2);

        assertThat(account1, is(notNullValue()));
        assertThat(account1.getBalance(), is(balance));
        assertThat(account2, is(notNullValue()));
        assertThat(account2.getBalance(), is(balance));

        this.bank.transferMoneyFromTo(accountId1, accountId2, balance, "Test transfer");
        account1 = this.bank.getAccount(accountId1);
        account2 = this.bank.getAccount(accountId2);

        assertThat(account1, is(notNullValue()));
        assertThat(account1.getBalance(), is(new BigDecimal(0)));
        assertThat(account2, is(notNullValue()));
        assertThat(account2.getBalance(), is(new BigDecimal(2000)));

        assertThat(account1.getTransactions(), is(notNullValue()));
        assertThat(account1.getTransactions().size(), is(1));
        Transaction transaction1 = account1.getTransactions().get(0);
        assertThat(transaction1.getAmount(), is(balance));
        assertThat(transaction1.getBalance(), is(new BigDecimal(0)));
        assertThat(transaction1.getCreditor(), is(accountId2));
        assertThat(transaction1.getDebtor(), is("this account"));
        assertThat(transaction1.getDescription(), is("Test transfer"));

        assertThat(account2.getTransactions(), is(notNullValue()));
        assertThat(account2.getTransactions().size(), is(1));
        Transaction transaction2 = account2.getTransactions().get(0);
        assertThat(transaction2.getAmount(), is(balance));
        assertThat(transaction2.getBalance(), is(new BigDecimal(2000)));
        assertThat(transaction2.getCreditor(), is("this account"));
        assertThat(transaction2.getDebtor(), is(accountId1));
        assertThat(transaction2.getDescription(), is("Test transfer"));
    }

    @After
    public void tearDown() throws BankServiceException_Exception {
        for (String id : this.createdAccounts)
            this.bank.retireAccount(id);
        this.createdAccounts.clear();
    }
}

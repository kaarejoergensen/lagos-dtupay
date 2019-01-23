import base.RPCServer;
import clients.BankClient;
import exceptions.ClientException;
import models.Account;
import models.AccountInfo;
import models.Transaction;
import models.User;
import org.junit.*;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;



/**
 * @author Kåre
 */
public class BankRPCTest {


    public static void main(String args[]){

    }


    /*
    private BankClient bank;
    private List<String> createdAccounts;

    public BankRPCTest() throws TimeoutException, IOException {
        createdAccounts = new ArrayList<>();
        bank = new BankClient(rabbitmq.getContainerIpAddress(), Server.RPC_QUEUE_NAME + "-test",
                "rabbitmq", "rabbitmq", rabbitmq.getFirstMappedPort());
    }

    @ClassRule
    public static GenericContainer rabbitmq = new GenericContainer<>("rabbitmq").withExposedPorts(5672)
            .withEnv("RABBITMQ_DEFAULT_USER", "rabbitmq").withEnv("RABBITMQ_DEFAULT_PASS", "rabbitmq")
            .waitingFor(Wait.forLogMessage(".*Server startup complete.*", 1));

    @BeforeClass
    public static void initServer() {
        RPCServer rpcServer = new Server();
        System.out.println("Starting server");
        new Thread(() -> {
            try {
                rpcServer.run(rabbitmq.getContainerIpAddress(), Server.RPC_QUEUE_NAME + "-test",
                        "rabbitmq", "rabbitmq", rabbitmq.getFirstMappedPort());
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
                Assert.fail();
            }
        }).start();
        System.out.println("Started in new thread");
    }

    @Test
    public void createAccount() throws ClientException {
        System.out.println("Entering createAccount");
        User user = new User("995566-2233","FirstName","LastName");
        BigDecimal balance = new BigDecimal(1000);

        this.ensureUserDoesntExist(user);
        String accountId = this.bank.createAccountWithBalance(user, balance);
        assertThat(accountId, is(notNullValue()));
        assertThat(accountId, is(not("")));
        this.createdAccounts.add(accountId);
    }

    @Test
    public void getAccount() throws ClientException {
        User user = new User("995566-2233","FirstName","LastName");
        BigDecimal balance = new BigDecimal(1000);

        this.ensureUserDoesntExist(user);
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
    public void getAccountByCPR() throws ClientException {
        User user = new User("995566-2233","FirstName","LastName");
        BigDecimal balance = new BigDecimal(1000);

        this.ensureUserDoesntExist(user);
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
    public void getAccounts() throws ClientException {
        User user = new User("995566-2233","FirstName","LastName");
        BigDecimal balance = new BigDecimal(1000);

        this.ensureUserDoesntExist(user);
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

    @Test(expected = ClientException.class)
    public void retireAccount() throws ClientException {
        User user = new User("995566-2233","FirstName","LastName");
        BigDecimal balance = new BigDecimal(1000);

        this.ensureUserDoesntExist(user);
        String accountId = this.bank.createAccountWithBalance(user, balance);
        assertThat(accountId, is(notNullValue()));
        assertThat(accountId, is(not("")));

        this.bank.retireAccount(accountId);
        this.bank.getAccount(accountId);
    }

    @Test
    public void transferMoneyFromTo() throws ClientException {
        User user1 = new User("995566-2233","FirstName","LastName");
        User user2 = new User("662288-5522","FirstName","LastName");
        BigDecimal balance = new BigDecimal(1000);

        this.ensureUserDoesntExist(user1);
        this.ensureUserDoesntExist(user2);
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

    private void ensureUserDoesntExist(User user) {
        try {
            Account account = this.bank.getAccountByCprNumber(user.getCprNumber());
            this.bank.retireAccount(account.getId());
        } catch (ClientException e) {
        }
    }

    @After
    public void tearDown() {
        for (String id : this.createdAccounts) {
            try {
                this.bank.retireAccount(id);
            } catch (ClientException e) {
            }
        }
        this.createdAccounts.clear();
    }
    */
}

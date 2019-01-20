package clients;

import base.Method.Bank;
import base.RPCClient;
import exceptions.ClientException;
import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.GsonBuilder;
import gherkin.deps.com.google.gson.reflect.TypeToken;
import models.Account;
import models.AccountInfo;
import models.User;
import utils.JSONMapper;
import utils.XMLGregorianCalendarConverter;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class BankClient extends Client {
    private static final String RPC_QUEUE_NAME = "rpc_queue_bank";
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter.Deserializer())
            .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter.Serializer())
            .create();

    public BankClient(List<String> hosts) throws IOException, TimeoutException {
        super(hosts, RPC_QUEUE_NAME);
    }

    public BankClient(String host) throws IOException, TimeoutException {
        super(Collections.singletonList(host), RPC_QUEUE_NAME);
    }

    public BankClient(String host, String username, String password) throws IOException, TimeoutException {
        super(Collections.singletonList(host), RPC_QUEUE_NAME, username, password);
    }

    public BankClient(String host, String queue) throws IOException, TimeoutException {
        super(host, queue);
    }

    public BankClient(List<String> hosts, String queue) throws IOException, TimeoutException {
        super(hosts, queue);
    }

    public BankClient(String host, String queue, String username, String password) throws IOException, TimeoutException {
        super(host, queue, username, password);
    }

    public BankClient(List<String> hosts, String queue, String username, String password) throws IOException, TimeoutException {
        super(hosts, queue, username, password);
    }

    public String createAccountWithBalance(User user, BigDecimal balance) throws ClientException {
        try {
            String result = this.rpcClient.call(Bank.createAccountWithBalance.toString(), gson.toJson(user), gson.toJson(balance));
            return gson.fromJson(result, String.class);
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public Account getAccount(String id) throws ClientException {
        try {
            String result = this.rpcClient.call(Bank.getAccount.toString(), id);
            return gson.fromJson(result, Account.class);
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public Account getAccountByCprNumber(String cprNumber) throws ClientException {
        try {
            String result = this.rpcClient.call(Bank.getAccountByCprNumber.toString(), cprNumber);
            return gson.fromJson(result, Account.class);
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public List<AccountInfo> getAccounts() throws ClientException {
        try {
            String result = this.rpcClient.call(Bank.getAccounts.toString());
            Type listType = new TypeToken<List<AccountInfo>>(){}.getType();
            return gson.fromJson(result, listType);
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public void retireAccount(String id) throws ClientException {
        try {
            String result = this.rpcClient.call(Bank.retireAccount.toString(), id);
            if (!JSONMapper.JSONToBoolean(result))
                throw new ClientException("Call failed: " + result);
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public void transferMoneyFromTo(String fromAccountId, String toAccountId, BigDecimal amount, String description) throws ClientException {
        try {
            String result = this.rpcClient.call(Bank.transferMoneyFromTo.toString(), fromAccountId, toAccountId, gson.toJson(amount), description);
            if (!JSONMapper.JSONToBoolean(result))
                throw  new ClientException("Call failed: " + result);
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }
}

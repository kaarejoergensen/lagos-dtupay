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
import java.util.List;
import java.util.concurrent.TimeoutException;

public class BankClient {
    protected static final String RPC_QUEUE_NAME = "rpc_queue_bank";
    private RPCClient rpcClient;
    private Gson gson;

    public BankClient(String host) throws IOException, TimeoutException {
        this(host, RPC_QUEUE_NAME);
    }

    public BankClient(String host, String queue) throws IOException, TimeoutException {
        rpcClient = new RPCClient(host, queue);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter.Deserializer())
                .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter.Serializer())
                .create();
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

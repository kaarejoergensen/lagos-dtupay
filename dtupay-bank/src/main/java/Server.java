import bank.Bank;
import bank.BankSOAP;
import bank.dtu.ws.fastmoney.exceptions.BankServiceException_Exception;
import base.Method;
import base.RPCServer;
import gherkin.deps.com.google.gson.Gson;
import models.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

public class Server extends RPCServer {
    static final String RPC_QUEUE_NAME = "rpc_queue_bank";
    private Bank bank = new BankSOAP();
    private Gson gson = new Gson();

    public static void main(String[] args) throws IOException, TimeoutException {
        if (args.length < 1) {
            System.out.println("Usage: app.jar brokerHost");
            return;
        }
        RPCServer rpcServer = new Server();
        rpcServer.run(args[0], RPC_QUEUE_NAME);
    }

    @Override
    protected String implementation(String... arguments) {
        if (arguments.length < 1)
            return this.error("Too few arguments!");
        try {
            return this.call(arguments);
        } catch (BankServiceException_Exception e) {
            return this.error(e.getMessage());
        }
    }

    private String call(String... arguments) throws BankServiceException_Exception {
        String method = arguments[0];
        Object result;
        switch (Method.Bank.valueOf(method)) {
            case createAccountWithBalance:
                result = this.bank.createAccountWithBalance(gson.fromJson(arguments[1], User.class), gson.fromJson(arguments[2], BigDecimal.class));
                break;
            case getAccount:
                result = bank.getAccount(arguments[1]);
                break;
            case getAccountByCprNumber:
                result = bank.getAccountByCprNumber(arguments[1]);
                break;
            case getAccounts:
                result = bank.getAccounts();
                break;
            case retireAccount:
                this.bank.retireAccount(arguments[1]);
                result = true;
                break;
            case transferMoneyFromTo:
                this.bank.transferMoneyFromTo(arguments[1], arguments[2], gson.fromJson(arguments[3], BigDecimal.class), arguments[4]);
                result = true;
                break;
            default:
                return this.error("No method found: " + method);
        }
        return this.convertToJson(result);
    }
}
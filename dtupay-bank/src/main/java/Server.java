import bank.Bank;
import bank.BankSOAP;
import bank.dtu.ws.fastmoney.exceptions.BankServiceException_Exception;
import base.RPCServer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Server extends RPCServer {
    private static final String RPC_QUEUE_NAME = "rpc_queue_bank";
    private Bank bank = new BankSOAP();

    public static void main(String[] args) throws IOException, TimeoutException {
        RPCServer rpcServer = new Server();
        rpcServer.run(args[0], RPC_QUEUE_NAME);
    }

    @Override
    protected String implementation(String... arguments) {
        if (arguments.length < 2)
            return this.error("Server error!");
        try {
            return this.call(arguments);
        } catch (BankServiceException_Exception e) {
            return this.error(e.getMessage());
        }
    }

    private String call(String... arguments) throws BankServiceException_Exception {
        String method = arguments[0];
        Object result;
        switch (method) {
            case "createAccountWithBalance":
                result = null;
                break;
            case "getAccount":
                result = bank.getAccount(arguments[1]);
                break;
            case "getAccountByCprNumber":
                result = bank.getAccountByCprNumber(arguments[1]);
                break;
            case "getAccounts":
                result = null;
                break;
            case "retireAccount":
                result = null;
                break;
            case "transferMoneyFromTo":
                result = null;
                break;
            default:
                return this.error("No method found: " + method);
        }
        return this.convertToJson(result);
    }
}
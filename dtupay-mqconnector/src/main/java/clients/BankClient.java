package clients;

import base.RPCClient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BankClient {
    private RPCClient rpcClient;
    public BankClient() throws IOException, TimeoutException {
        rpcClient = new RPCClient();
    }
}

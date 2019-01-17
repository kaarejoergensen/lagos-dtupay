package clients;

import base.RPCClient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BankClient {
    private static final String RPC_QUEUE_NAME = "rpc_queue_bank";
    private RPCClient rpcClient;

    public BankClient(String host) throws IOException, TimeoutException {
        rpcClient = new RPCClient(host, RPC_QUEUE_NAME);
    }
}

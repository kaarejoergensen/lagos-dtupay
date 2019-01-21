package clients;

import base.RPCClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class Client {
    RPCClient rpcClient;

    public Client(String host, String queue) throws IOException, TimeoutException {
        rpcClient = new RPCClient(host, queue);
    }

    public Client(List<String> hosts, String queue) throws IOException, TimeoutException {
        rpcClient = new RPCClient(hosts, queue);
    }

    public Client(String host, String queue, String username, String password) throws IOException, TimeoutException {
        rpcClient = new RPCClient(host, queue, username, password);
    }

    public Client(List<String> hosts, String queue, String username, String password) throws IOException, TimeoutException {
        rpcClient = new RPCClient(hosts, queue, username, password);
    }

    public Client(List<String> hosts, String queue, String username, String password, int port) throws IOException, TimeoutException {
        rpcClient = new RPCClient(hosts, queue, username, password, port);
    }
}

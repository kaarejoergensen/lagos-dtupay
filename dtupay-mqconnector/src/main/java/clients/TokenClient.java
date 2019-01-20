package clients;

import base.Method.Token;
import base.RPCClient;
import exceptions.ClientException;
import utils.JSONMapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class TokenClient extends Client {
    private static final String RPC_QUEUE_NAME = "rpc_queue_token";

    public TokenClient(List<String> hosts) throws IOException, TimeoutException {
        super(hosts, RPC_QUEUE_NAME);
    }

    public TokenClient(String host) throws IOException, TimeoutException {
        super(host, RPC_QUEUE_NAME);
    }

    public TokenClient(String host, String username, String password) throws IOException, TimeoutException {
        super(host, RPC_QUEUE_NAME, username, password);
    }

    public TokenClient(String host, String queue) throws IOException, TimeoutException {
        super(host, queue);
    }

    public TokenClient(List<String> hosts, String queue) throws IOException, TimeoutException {
        super(hosts, queue);
    }

    public TokenClient(String host, String queue, String username, String password) throws IOException, TimeoutException {
        super(host, queue, username, password);
    }

    public TokenClient(List<String> hosts, String queue, String username, String password) throws IOException, TimeoutException {
        super(hosts, queue, username, password);
    }

    public Set<String> getTokens(String userName, String userId, int numberOfTokens) throws ClientException {
        try {
            String result = this.rpcClient.call(Token.getTokens.toString(), userName, userId, String.valueOf(numberOfTokens));
            return new HashSet<>(Arrays.asList(JSONMapper.JSONToArray(result)));
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public boolean useToken(String tokenString) throws ClientException {
        try {
            String result = this.rpcClient.call(Token.useToken.toString(), tokenString);
            return JSONMapper.JSONToBoolean(result);
        } catch (InterruptedException | IOException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public boolean reset() throws ClientException {
        try {
            String result = this.rpcClient.call(Token.reset.toString());
            return JSONMapper.JSONToBoolean(result);
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }
}

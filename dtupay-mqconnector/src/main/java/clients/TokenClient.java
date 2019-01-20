package clients;

import base.Method.Token;
import base.RPCClient;
import exceptions.ClientException;
import utils.JSONMapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class TokenClient {
    private static final String RPC_QUEUE_NAME = "rpc_queue_token";
    private RPCClient rpcClient;

    public TokenClient(List<String> hosts) throws IOException, TimeoutException {
        this(hosts, RPC_QUEUE_NAME);
    }

    public TokenClient(String host) throws IOException, TimeoutException {
        this(Collections.singletonList(host), RPC_QUEUE_NAME);
    }

    public TokenClient(String host, String queue) throws IOException, TimeoutException {
        this(Collections.singletonList(host), queue);
    }


    public TokenClient(List<String> hosts, String queue) throws IOException, TimeoutException {
        rpcClient = new RPCClient(hosts, queue);
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

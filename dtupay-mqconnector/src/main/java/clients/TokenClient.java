package clients;

import base.RPCClient;
import exceptions.ClientException;
import utils.JSONMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class TokenClient {
    private static final String RPC_QUEUE_NAME = "rpc_queue_token";
    private RPCClient rpcClient;

    public TokenClient(String host) throws IOException, TimeoutException {
        rpcClient = new RPCClient(host, RPC_QUEUE_NAME);
    }

    public Set<String> getTokens(String userName, String userId, int numberOfTokens) throws ClientException {
        try {
            String result = this.rpcClient.call("getTokens", userName, userId, String.valueOf(numberOfTokens));
            return new HashSet<>(Arrays.asList(JSONMapper.JSONToArray(result)));
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public boolean useToken(String tokenString) throws ClientException {
        try {
            String result = this.rpcClient.call("useToken", tokenString);
            return JSONMapper.JSONToBoolean(result);
        } catch (InterruptedException | IOException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }
}

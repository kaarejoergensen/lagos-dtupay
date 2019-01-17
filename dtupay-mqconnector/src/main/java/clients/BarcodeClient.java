package clients;

import base.RPCClient;
import exceptions.ClientException;
import utils.JSONMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class BarcodeClient {
    private RPCClient rpcClient;
    public BarcodeClient() throws IOException, TimeoutException {
        rpcClient = new RPCClient();
    }

    public Optional<Set<String>> getTokens(String userName, String userId, int numberOfTokens) {
        try {
            String result = this.rpcClient.call("getTokens", userName, userId, String.valueOf(numberOfTokens));
            return Optional.of(new HashSet<>(Arrays.asList(JSONMapper.JSONToArray(result))));
        } catch (IOException | InterruptedException | ClientException e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Boolean> useToken(String tokenString) {
        try {
            String result = this.rpcClient.call("useToken", tokenString);
            return Optional.of(JSONMapper.JSONToBoolean(result));
        } catch (InterruptedException | IOException | ClientException e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }
}

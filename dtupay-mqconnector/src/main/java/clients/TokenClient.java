package clients;

import base.Method.Token;
import base.RPCClient;
import exceptions.ClientException;
import utils.JSONMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;
/**
 * @author Kåre
 */
public class TokenClient extends RPCClient {
    private static final String RPC_QUEUE_NAME = "rpc_queue_token";

    public TokenClient(String host, String queueName) throws IOException, TimeoutException {
        super(host, queueName);
    }

    public TokenClient(String host, String username, String password) throws IOException, TimeoutException {
        super(host, RPC_QUEUE_NAME, username, password);
    }

    public TokenClient(String host, String queueName, String username, String password) throws IOException, TimeoutException {
        super(host, queueName, username, password);
    }

    public TokenClient(String host, String queueName, String username, String password, int port) throws IOException, TimeoutException {
        super(host, queueName, username, password, port);
    }

    public Set<String> getTokens(String userName, String userId, int numberOfTokens) throws ClientException {
        try {
            String result = this.call(Token.getTokens.toString(), userName, userId, String.valueOf(numberOfTokens));
            return new HashSet<>(Arrays.asList(JSONMapper.JSONToArray(result)));
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public boolean useToken(String tokenString) throws ClientException {
        try {
            String result = this.call(Token.useToken.toString(), tokenString);
            return JSONMapper.JSONToBoolean(result);
        } catch (InterruptedException | IOException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public boolean reset() throws ClientException {
        try {
            String result = this.call(Token.reset.toString());
            return JSONMapper.JSONToBoolean(result);
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public String getUserIdFromToken(String token) throws ClientException {
        try {
            return this.call(Token.getUserIdFromToken.toString(), token);
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }
}

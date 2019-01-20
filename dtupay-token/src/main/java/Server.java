import base.Method;
import base.RPCServer;
import persistence.MemoryDataStore;
import persistence.MongoDataStore;
import tokens.TokenProvider;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Server extends RPCServer {
    protected static final String RPC_QUEUE_NAME = "rpc_queue_token";
    private TokenProvider tokenProvider;

    public static void main(String[] args) throws IOException, TimeoutException {
        final TokenProvider tokenProvider = new TokenProvider(new MemoryDataStore());
        RPCServer rpcServer = new Server(tokenProvider);
        rpcServer.run(args[0], RPC_QUEUE_NAME);
    }

    Server(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected String implementation(String... arguments) {
        if (arguments.length < 1)
            return this.error("Server error!");
        try {
            return this.call(arguments);
        } catch (IllegalArgumentException e) {
            return this.error(e.getMessage());
        }
    }

    private String call(String... arguments) {
        String method = arguments[0];
        Object result;
        switch (Method.Token.valueOf(method)) {
            case getTokens:
                result = tokenProvider.getTokens(arguments[1], arguments[2], Integer.parseInt(arguments[3]));
                break;
            case useToken:
                result = tokenProvider.useToken(arguments[1]);
                break;
            case reset:
                tokenProvider.reset();
                result = true;
                break;
            default:
                return this.error("No method found: " + method);
        }
        return this.convertToJson(result);
    }
}

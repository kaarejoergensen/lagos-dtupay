import base.RPCServer;
import persistence.MemoryDataStore;
import tokens.TokenProvider;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Server extends RPCServer {
    private TokenProvider tokenProvider;

    public static void main(String[] args) throws IOException, TimeoutException {
        final TokenProvider tokenProvider = new TokenProvider(new MemoryDataStore());
        RPCServer rpcServer = new Server(tokenProvider);
        rpcServer.run(args[0]);
    }

    private Server(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected String implementation(String... arguments) {
        if (arguments.length < 2)
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
        switch (method) {
            case "getTokens":
                result = tokenProvider.getTokens(arguments[1], arguments[2], Integer.parseInt(arguments[3]));
                break;
            case "useToken":
                result = tokenProvider.useToken(arguments[1]);
                break;
            default:
                return this.error("No method found");
        }
        return this.convertToJson(result);
    }
}

import base.RPCServer;
import persistence.MemoryDataStore;
import tokens.TokenProvider;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Server {
    public static void main(String[] args) {
        final TokenProvider tokenProvider = new TokenProvider(new MemoryDataStore());
        RPCServer rpcServerToken = new RPCServer() {
            @Override
            protected String implementation(String... arguments) {
                if (arguments.length < 2)
                    return null;
                String method = arguments[0];
                Object result;
                switch (method) {
                    case "getTokens":
                        try {
                            result = tokenProvider.getTokens(arguments[1], arguments[2], Integer.parseInt(arguments[3]));
                        } catch (IllegalArgumentException e) {
                            return this.error(e.getMessage());
                        }
                        break;
                    case "useToken":
                        result = tokenProvider.useToken(arguments[1]);
                        break;
                    default:
                        return null;
                }

                return this.convertToJson(result);
            }
        };
        try {
            rpcServerToken.run();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}

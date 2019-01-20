import base.RPCServer;
import clients.TokenClient;
import exceptions.ClientException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import persistence.MongoDataStore;
import tokens.TokenProvider;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TokenRPCTest {
    private static final String RABBITMQ_HOSTNAME = "localhost";     // Should always be "rabbitmq" for jenkins.
                                                                    // Use localhost when running locally
    private static final String MONGO_HOSTNAME = "mongo";           // Should always be "mongo" for prod.
                                                                    // Use localhost when running locally
    private String userName = "core";
    private String userId = "1234";

    private TokenClient tokenClient;

    public TokenRPCTest() throws TimeoutException, InterruptedException {
        for (int i = 0; i < 6; i++) {
            try {
                tokenClient = new TokenClient(RABBITMQ_HOSTNAME, Server.RPC_QUEUE_NAME + "-test");
                System.out.println("Created TokenClient");
                break;
            } catch (IOException e) {
                System.out.println("Client: Connection refused, waiting 5 seconds");
                Thread.sleep(5000);
            }
        }
        if (tokenClient == null)
            throw new TimeoutException("Connection to broker could not be established!");
    }

    @BeforeClass
    public static void initServer() {
        TokenProvider tokenProvider = new TokenProvider(new MongoDataStore(MONGO_HOSTNAME));
        RPCServer rpcServer = new Server(tokenProvider);
        System.out.println("Starting server");
        new Thread(() -> {
            try {
                rpcServer.run(RABBITMQ_HOSTNAME, Server.RPC_QUEUE_NAME + "-test");
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("Started in new thread");
    }

    @Test
    public void issueTokens() throws ClientException {
        Set<String> tokens = tokenClient.getTokens(this.userName, this.userId, 5);
        assertThat(tokens, is(notNullValue()));
        assertThat(tokens.size(), is(5));
        for (String token : tokens) {
            assertThat(tokenClient.useToken(token), is(true));
        }
    }

    @Test
    public void invalidToken() throws ClientException {
        assertThat(tokenClient.useToken("1234"), is(false));
    }

    @Test
    public void tamperedToken() throws ClientException {
        this.tokenClient.reset();
        Set<String> tokens = tokenClient.getTokens(this.userName, this.userId, 1);
        assertThat(tokens, is(notNullValue()));
        assertThat(tokens.size(), is(1));
        assertThat(tokenClient.useToken(tokens.iterator().next() + "1234"), is(false));
    }

    @Test(expected = ClientException.class)
    public void maxFiveTokens() throws ClientException {
        tokenClient.getTokens(this.userName, this.userId, 6);
    }

    @Test(expected = ClientException.class)
    public void atLeastOneToken() throws ClientException {
        tokenClient.getTokens(this.userName, this.userId, 0);
    }

    @Test
    public void useToken() throws ClientException {
        Set<String> tokens = tokenClient.getTokens(this.userName, this.userId, 1);
        assertThat(tokens, is(notNullValue()));
        assertThat(tokens.size(), is(1));
        assertThat(tokenClient.useToken(tokens.iterator().next()), is(true));
        assertThat(tokenClient.useToken(tokens.iterator().next()), is(false));
    }

    @Test
    public void getTokenIfOnlyOneUnused() throws ClientException {
        Set<String> tokens = tokenClient.getTokens(this.userName, this.userId, 1);
        tokens.addAll(tokenClient.getTokens(this.userName, this.userId, 1));
        assertThat(tokens, is(notNullValue()));
        assertThat(tokens.size(), is(2));
        assertThat(tokenClient.useToken(tokens.iterator().next()), is(true));
        tokens.addAll(tokenClient.getTokens(this.userName, this.userId, 1));
    }

    @Test(expected = ClientException.class)
    public void maxOneUnusedToken() throws ClientException {
        tokenClient.getTokens(this.userName, this.userId, 2);
        tokenClient.getTokens(this.userName, this.userId, 1);
    }

    @After
    public void deleteTokens() throws ClientException {
        this.tokenClient.reset();
    }
}

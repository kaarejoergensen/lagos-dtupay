import base.RPCServer;
import clients.TokenClient;
import exceptions.ClientException;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import persistence.Datastore;
import persistence.MemoryDataStore;
import persistence.MongoDataStore;
import tokens.TokenProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TokenRPCTest {
    private static final String RABBITMQ_HOSTNAME = "rabbitmq";     // Should always be "rabbitmq" for jenkins.
    private static final List<String> RABBITMQ_HOSTS = Arrays.asList(RABBITMQ_HOSTNAME, "localhost");
    private static final String MONGO_HOSTNAME = "mongo";           // Should always be "mongo" for jenkins.
    private static final List<String> MONGO_HOSTS = Arrays.asList(MONGO_HOSTNAME, "localhost");

    private String userName = "core";
    private String userId = "1234";
    private TokenClient tokenClient;

    public TokenRPCTest() throws TimeoutException, IOException {
        tokenClient = new TokenClient(RABBITMQ_HOSTS, Server.RPC_QUEUE_NAME + "-test");
    }

    @BeforeClass
    public static void initServer() throws IOException {
        TokenProvider tokenProvider = new TokenProvider(new MongoDataStore(MONGO_HOSTS));
        RPCServer rpcServer = new Server(tokenProvider);
        System.out.println("Starting server");
        new Thread(() -> {
            try {
                rpcServer.run(RABBITMQ_HOSTS, Server.RPC_QUEUE_NAME + "-test");
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
                Assert.fail();
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

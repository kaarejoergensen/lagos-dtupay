import base.RPCServer;
import clients.TokenClient;
import exceptions.ClientException;
import org.junit.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import persistence.Datastore;
import persistence.MongoDataStore;
import tokens.TokenProvider;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TokenRPCTest {
    private String userName = "core";
    private String userId = "1234";
    private TokenClient tokenClient;

    public TokenRPCTest() throws TimeoutException, IOException {
        tokenClient = new TokenClient(rabbitmq.getContainerIpAddress(), Server.RPC_QUEUE_NAME + "-test",
                    "rabbitmq", "rabbitmq", rabbitmq.getFirstMappedPort());
    }

    @ClassRule
    public static GenericContainer mongo = new GenericContainer<>("mongo").withExposedPorts(27017)
            .waitingFor(Wait.forLogMessage(".*waiting for connections on port 27017.*", 1));

    @ClassRule
    public static GenericContainer rabbitmq = new GenericContainer<>("rabbitmq").withExposedPorts(5672)
            .withEnv("RABBITMQ_DEFAULT_USER", "rabbitmq").withEnv("RABBITMQ_DEFAULT_PASS", "rabbitmq")
            .waitingFor(Wait.forLogMessage(".*Server startup complete.*", 1));

    @BeforeClass
    public static void initServer() {
        Datastore datastore = new MongoDataStore(mongo.getContainerIpAddress(), mongo.getFirstMappedPort());
        TokenProvider tokenProvider = new TokenProvider(datastore);
        RPCServer rpcServer = new Server(tokenProvider);
        System.out.println("Starting server");
        new Thread(() -> {
            try {
                rpcServer.run(rabbitmq.getContainerIpAddress(),Server.RPC_QUEUE_NAME + "-test",
                        "rabbitmq", "rabbitmq", rabbitmq.getFirstMappedPort());
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
            assertThat(tokenClient.getUserIdFromToken(token), is(this.userId));
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

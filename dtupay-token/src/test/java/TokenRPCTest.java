import base.RPCServer;
import clients.TokenClient;
import exceptions.ClientException;
import org.junit.After;
import org.junit.Test;
import persistence.MemoryDataStore;
import tokens.TokenProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TokenRPCTest {
    private List<String> tokensToBeUsed = new ArrayList<>();

    private String userName = "core";
    private String userId = "1234";

    private TokenClient tokenClient;

    public TokenRPCTest() throws TimeoutException, InterruptedException {
        TokenProvider tokenProvider = new TokenProvider(new MemoryDataStore());
        RPCServer rpcServer = new Server(tokenProvider);
        System.out.println("Starting server");
        new Thread(() -> {
            try {
                rpcServer.run("rabbitmq", Server.RPC_QUEUE_NAME + "-test");
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("Started in new thread");
        for (int i = 0; i < 6; i++) {
            try {
                tokenClient = new TokenClient("rabbitmq", Server.RPC_QUEUE_NAME + "-test");
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
        Set<String> tokens = tokenClient.getTokens(this.userName, this.userId, 1);
        assertThat(tokens, is(notNullValue()));
        assertThat(tokens.size(), is(1));
        assertThat(tokenClient.useToken(tokens.iterator().next() + "1234"), is(false));
        this.tokensToBeUsed.addAll(tokens);
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
        tokenClient.useToken(tokens.iterator().next());
        tokens.addAll(tokenClient.getTokens(this.userName, this.userId, 1));
        tokensToBeUsed.addAll(tokens);
    }

    @Test(expected = ClientException.class)
    public void maxOneUnusedToken() throws ClientException {
        tokensToBeUsed.addAll(tokenClient.getTokens(this.userName, this.userId, 2));
        tokenClient.getTokens(this.userName, this.userId, 1);
    }

    @After
    public void deleteTokens() throws ClientException {
        for (String token : this.tokensToBeUsed)
            this.tokenClient.useToken(token);
        this.tokensToBeUsed.clear();
    }
}

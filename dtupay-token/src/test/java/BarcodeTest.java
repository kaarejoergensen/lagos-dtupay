import org.junit.Before;
import org.junit.Test;
import persistence.Datastore;
import persistence.MemoryDataStore;
import tokens.TokenProvider;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
/**
 * @author Kåre
 */
public class BarcodeTest {
    private String userName = "core";
    private String userId = "1234";

    private TokenProvider tokenProvider;
    private Datastore datastore;

    @Before
    public void init() {
        this.datastore = new MemoryDataStore();
        this.tokenProvider = new TokenProvider(this.datastore);
    }

    @Test
    public void issueTokens() {
        Set<String> tokens = tokenProvider.getTokens(this.userName, this.userId, 5);
        assertThat(tokens, is(notNullValue()));
        assertThat(tokens.size(), is(5));
        for (String token : tokens) {
            assertThat(tokenProvider.useToken(token), is(true));
        }
    }

    @Test
    public void invalidToken() {
        assertThat(tokenProvider.useToken("1234"), is(false));
    }

    @Test
    public void tamperedToken() {
        Set<String> tokens = tokenProvider.getTokens(this.userName, this.userId, 1);
        assertThat(tokens, is(notNullValue()));
        assertThat(tokens.size(), is(1));
        assertThat(tokenProvider.useToken(tokens.iterator().next() + "1234"), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void maxFiveTokens() {
        tokenProvider.getTokens(this.userName, this.userId, 6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void atLeastOneToken() {
        tokenProvider.getTokens(this.userName, this.userId, 0);
    }

    @Test
    public void useToken() {
        Set<String> tokens = tokenProvider.getTokens(this.userName, this.userId, 1);
        assertThat(tokens, is(notNullValue()));
        assertThat(tokens.size(), is(1));
        assertThat(datastore.getNumberOfUnusedTokens(this.userId), is(1));
        assertThat(tokenProvider.useToken(tokens.iterator().next()), is(true));
        assertThat(datastore.getNumberOfUnusedTokens(this.userId), is(0));
        assertThat(tokenProvider.useToken(tokens.iterator().next()), is(false));
    }

    @Test
    public void getTokenIfOnlyOneUnused() {
        Set<String> tokens = tokenProvider.getTokens(this.userName, this.userId, 1);
        tokens.addAll(tokenProvider.getTokens(this.userName, this.userId, 1));
        assertThat(tokens, is(notNullValue()));
        assertThat(tokens.size(), is(2));
        tokenProvider.useToken(tokens.iterator().next());
        tokenProvider.getTokens(this.userName, this.userId, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void maxOneUnusedToken() {
        tokenProvider.getTokens(this.userName, this.userId, 2);
        assertThat(datastore.getNumberOfUnusedTokens(this.userId), is(2));
        tokenProvider.getTokens(this.userName, this.userId, 1);
    }

}

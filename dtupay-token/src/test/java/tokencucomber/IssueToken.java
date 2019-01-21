package tokencucomber;

import cucumber.api.PendingException;
import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import persistence.Datastore;
import persistence.MemoryDataStore;
import persistence.MongoDataStore;
import tokens.TokenProvider;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class IssueToken {

    private String userName;
    private String userId;

    private int numberOfTokens;

    private Datastore datastore;
    private TokenProvider tokenProvider;

    private Set<String> tokens;

    @Given("^the user id, user name and the number of tokens$")
    public void the_user_id_name_numberOfTokens() {
         userName = "Fred";
         userId = "1234";
         numberOfTokens = 5;
         datastore = new MemoryDataStore();
         tokenProvider = new TokenProvider(datastore);
    }

    @When("^issuing tokens$")
    public void issuing_tokens() {
        tokens = tokenProvider.getTokens(userName,userId,numberOfTokens);
    }

    @Then("^the tokens is not null")
    public void the_tokens_is_not_null(){
        assertThat(tokens, is(notNullValue()) );
    }


    @Then("^the token count provided should be \"([^\"]*)\"$")
    public void the_token_count_provided_should_be(int expectedValue){
        assertEquals(tokens.size(), expectedValue);
    }

    @Then("^the user is able to use the tokens")
    public void the_user_is_able_to_use_the_tokens(){
        for (String token : tokens) {
            assertThat(tokenProvider.useToken(token), is(true));
        }
    }

    @Then("^not use tokens beyond the provided ones")
    public void not_use_tokens_beyond_the_provided_ones(){
        assertThat(tokenProvider.useToken("1234"), is(false));
    }

    /*
    SCENARIO 2
     */

    @Given("^username \"([^\"]*)\", userId \"([^\"]*)\" and \"([^\"]*)\" number of tokens$")
    public void tampring_given_username_id_nrtokens(String username, String userid, int nrOfTokens ) {
        this.userName = username;
        this.userId = userid;
        this.numberOfTokens = nrOfTokens;
    }

    @When("^retreving a set of tokens$")
    public void tampring_retriving_tokens() {
        datastore = new MemoryDataStore();
        tokenProvider = new TokenProvider(datastore);
        tokens = this.tokenProvider.getTokens(this.userName, this.userId, this.numberOfTokens);
    }

    @Then("^the token count is indeed \"([^\"]*)\"$")
    public void tampring_check_number_and_notnull(int expectedNr){
        assertEquals(tokens.size(), expectedNr);
    }

    @Then("^the user can't tamper the token  - use a non existing token by injecting userid$")
    public void cant_tamper_token(){
        assertThat(tokenProvider.useToken(tokens.iterator().next() + this.userId), is(false));
    }





}

package tokencucomber;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import persistence.Datastore;
import persistence.MemoryDataStore;
import tokens.TokenProvider;

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


    @Then("^the tokens provided should be \"([^\"]*)\"$")
    public void the_tokens_provided_should_not_be(int expectedValue){
        assertEquals(tokens.size(), expectedValue);
    }




}

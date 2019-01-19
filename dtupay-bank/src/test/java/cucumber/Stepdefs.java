package cucumber;



import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;
import static org.junit.Assert.*;

class IsItFriday {
    static String isItFriday(String today) {
        return "Nope";
    }
}

public class Stepdefs {
    private String today;
    private String actualAnswer;
    private String cpr;
    private String firstName;
    private String lastName;



    @Given("^today is Sunday$")
    public void today_is_Sunday() {
        today = "Sunday";
    }

    @When("^I ask whether it's Friday yet$")
    public void i_ask_whether_is_s_Friday_yet() {
        actualAnswer = IsItFriday.isItFriday(today);
    }

    @Then("^I should be told \"([^\"]*)\"$")
    public void i_should_be_told(String expectedAnswer) {
        System.out.println("Is it friday - Working");
        assertEquals(expectedAnswer, actualAnswer);
    }




    @Given("that the user has cprNumber {string}")
    public void that_the_user_has_cprNumber(String cprInput) {
        cpr = cprInput;
    }

    @Given("that the user has firstName {string}")
    public void that_the_user_has_firstName(String firstNameInput) {
        firstName = firstNameInput;

    }

    @Given("that the user has lastName {string}")
    public void that_the_user_has_lastName(String lastNameInput) {
        lastName = lastNameInput;
    }

    @Then("user cpr should be {string}")
    public void user_cpr_should_be(String expectedAnswer) {
        System.out.println("User cpr check - Working");
        assertEquals(expectedAnswer, cpr);
    }

    @Given("that the balance is {string}")
    public void that_the_balance_is(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("the user creates account")
    public void the_user_creates_account() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the account is not {string}")
    public void the_account_is_not(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the account id is not {string}")
    public void the_account_id_is_not(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }




}
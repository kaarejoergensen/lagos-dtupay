package cucumber;


import bank.Bank;
import bank.BankSOAP;
import bank.dtu.ws.fastmoney.exceptions.BankServiceException_Exception;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.User;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;



public class Stepdefs {

    private BigDecimal balance;
    private Bank bank = new BankSOAP();
    private User user = new User();


    @Given("that the user has cprNumber {string}")
    public void that_the_user_has_cprNumber(String cprInput) {
        user.setCprNumber(cprInput);
    }

    @Given("that the user has firstName {string}")
    public void that_the_user_has_firstName(String firstNameInput) {
        user.setFirstName(firstNameInput);

    }

    @Given("that the user has lastName {string}")
    public void that_the_user_has_lastName(String lastNameInput) {
        user.setLastName(lastNameInput);
    }

    @Then("user cpr should be {string}")
    public void user_cpr_should_be(String expectedAnswer) {
        System.out.println("User cpr check - Working");
        assertEquals(expectedAnswer, user.getCprNumber());
    }

    @Given("that the balance is {string}")
    public void that_the_balance_is(String balanceInput) {
        balance = new BigDecimal(Integer.parseInt(balanceInput));
    }

    @When("the user creates account")
    public void the_user_creates_account() {
        try {
            bank.createAccountWithBalance(user,balance);
        } catch (BankServiceException_Exception e) {
            e.printStackTrace();
        }
    }

    @Then("the account is not {string}")
    public void the_account_is_not(String nullInput) {
        try {
            assertNotSame(nullInput, bank.getAccountByCprNumber(user.getCprNumber()));
        } catch (BankServiceException_Exception e) {
            e.printStackTrace();
        }
    }

    @Then("the account id is not {string}")
    public void the_account_id_is_not(String emptyString) {
        try {
            assertNotSame(emptyString, bank.getAccountByCprNumber(user.getCprNumber()));
        } catch (BankServiceException_Exception e) {
            e.printStackTrace();
        }
    }

    @Given("the account is created")
    public void the_account_is_created() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("the user gets account")
    public void the_user_gets_account() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the account id is correct")
    public void the_account_id_is_correct() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the account balance is {string}")
    public void the_account_balance_is(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the account user is correct")
    public void the_account_user_is_correct() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the account transactions is empty")
    public void the_account_transactions_is_empty() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("the user gets account by cpr number")
    public void the_user_gets_account_by_cpr_number() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the account is not Null")
    public void the_account_is_not_Null() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("the user gets accounts")
    public void the_user_gets_accounts() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("account infos is not {string}")
    public void account_infos_is_not(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("account infos is not empty")
    public void account_infos_is_not_empty() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("account info is not Null")
    public void account_info_is_not_Null() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("account info user is correct")
    public void account_info_user_is_correct() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }


}
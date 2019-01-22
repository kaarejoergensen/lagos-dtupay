package cucumber;


import bank.Bank;
import bank.BankSOAP;
import bank.dtu.ws.fastmoney.exceptions.BankServiceException_Exception;
//import com.sun.org.apache.xpath.internal.operations.String;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Account;
import models.AccountInfo;
import models.User;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;


public class Stepdefs {

    private BigDecimal balance;
    private Bank bank = new BankSOAP();
    private User user = new User();

    public Stepdefs(){
        bank.getAccounts().clear();
    }

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
    public void the_user_creates_account() throws BankServiceException_Exception {
        //Removes account if it exists
        if(bank.accountExists(user.getCprNumber()) ) {
            bank.retireAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
        }
        bank.createAccountWithBalance(user,balance);

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
    public void the_account_is_created() throws BankServiceException_Exception {
        //Removes account if it exists
        if(bank.accountExists(user.getCprNumber()) ) {
            bank.retireAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
        }
        bank.createAccountWithBalance(user,balance);
    }

    @When("the user gets account")
    public void the_user_gets_account() throws BankServiceException_Exception {
        Account userAccount = bank.getAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
        assertNotNull(userAccount);
    }

    @Then("the account id is correct")
    public void the_account_id_is_correct() throws BankServiceException_Exception {
        String tempAccountId;
        if(bank.accountExists(user.getCprNumber()) ) {
            bank.retireAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
        }
        tempAccountId = this.bank.createAccountWithBalance(user,balance);
        String accountId = bank.getAccountByCprNumber(user.getCprNumber()).getId();
        assertEquals(tempAccountId, accountId);
    }

    @Then("the account balance is {string}")
    public void the_account_balance_is(String balanceInput) throws BankServiceException_Exception {
        Account tempAccount = bank.getAccountByCprNumber(user.getCprNumber());
        assertEquals(new BigDecimal(Integer.parseInt(balanceInput)),tempAccount.getBalance());

    }

    @Then("the account user is correct")
    public void the_account_user_is_correct() throws BankServiceException_Exception {
        Account tempAccount = bank.getAccountByCprNumber(user.getCprNumber());
        assertEquals(user.getFirstName(),tempAccount.getUser().getFirstName());
        assertEquals(user.getLastName(),tempAccount.getUser().getLastName());
        assertEquals(user.getCprNumber(),tempAccount.getUser().getCprNumber());
    }

    @Then("the account transactions is empty")
    public void the_account_transactions_is_empty() throws BankServiceException_Exception {
        Account tempAccount = bank.getAccountByCprNumber(user.getCprNumber());
        assertEquals(tempAccount.getTransactions().size(),0);

    }

    @When("the user gets account by cpr number")
    public void the_user_gets_account_by_cpr_number() throws BankServiceException_Exception {
        Account tempAccount = bank.getAccountByCprNumber(user.getCprNumber());
        assertEquals(tempAccount.getUser().getCprNumber(),user.getCprNumber());

    }

    @Then("the account is not Null")
    public void the_account_is_not_Null() throws BankServiceException_Exception {
        Account tempAccount = bank.getAccountByCprNumber(user.getCprNumber());
        assertNotNull(tempAccount);
    }

    @When("the user gets accounts")
    public void the_user_gets_accounts() throws BankServiceException_Exception {
        List<AccountInfo> accountList = bank.getAccounts();
        assertFalse( bank.getAccounts().isEmpty());
    }

    @Then("account infos is not {string}")
    public void account_infos_is_not(String string) throws BankServiceException_Exception {
        List<AccountInfo> accountList = bank.getAccounts();
        assertNotNull(accountList);
    }

    @Then("account infos is not empty")
    public void account_infos_is_not_empty() throws BankServiceException_Exception {
        assertFalse( bank.getAccounts().isEmpty());
    }

    @Then("account info is not Null")
    public void account_info_is_not_Null() throws BankServiceException_Exception {
        Account userAccount = bank.getAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
        assertNotNull(userAccount);
    }

    @Then("account info user is correct")
    public void account_info_user_is_correct() throws BankServiceException_Exception {
        Account userAccount = bank.getAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
        assertNotNull(userAccount);
    }


}
package cucumber;


import bank.Bank;
import bank.BankSOAP;
import bank.dtu.ws.fastmoney.exceptions.BankServiceException_Exception;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Account;
import models.AccountInfo;
import models.User;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

//import com.sun.org.apache.xpath.internal.operations.String;

/**
 * @author Asge
 */
public class Stepdefs {

    private BigDecimal balance;
    private Bank bank = new BankSOAP();
    private User user = new User();
    private HashMap<String,User> users = new HashMap<String,User>();
    private Boolean exceptionCatched = false;


    public Stepdefs(){
        bank.getAccounts().clear();

    }


    @Before
    public void before(Scenario scenario) {
        System.out.println("------------------------------");
        System.out.println("Testing - " + scenario.getName());
        System.out.println("------------------------------");
        users = new HashMap<String,User>();
        exceptionCatched = false;
        if(bank.accountExists("995566-2233") ) {
            try {
                bank.retireAccount(bank.getAccountByCprNumber("995566-2233").getId());
            } catch (BankServiceException_Exception e) {
                e.printStackTrace();
            }
        }
        if(bank.accountExists("662288-5522") ) {
            try {
                bank.retireAccount(bank.getAccountByCprNumber("662288-5522").getId());
            } catch (BankServiceException_Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Rule
    public ExpectedException exception = ExpectedException.none();


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
        //System.out.println("User:" + user.getCprNumber() + " " + user.getFirstName() + " " + user.getLastName());
        Account userAccount = bank.getAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
        assertNotNull(userAccount.getId());
    }

    @Then("account info user is correct")
    public void account_info_user_is_correct() throws BankServiceException_Exception {
        Account userAccount = bank.getAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
        assertNotNull(userAccount);
    }

    @When("the user creates an account that already exists")
    public void the_user_creates_an_account_that_already_exists() throws BankServiceException_Exception {
        if(bank.accountExists(user.getCprNumber()) ) {
            bank.retireAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
        }
        bank.createAccountWithBalance(user,balance);
        assertTrue(bank.accountExists(user.getCprNumber()));

    }

    @Then("the service returns a failure message when creating account")
    public void the_service_returns_a_failure_message_when_creating_account() {
        boolean exceptionCatched = false;
        try {
            bank.createAccountWithBalance(user,balance);
        } catch (BankServiceException_Exception e) {
            exceptionCatched = true;
        }
        assertTrue(exceptionCatched);

    }

    @Then("the service returns a failure message when getting account")
    public void the_service_returns_a_failure_message_when_getting_account() {
        boolean exceptionCatched = false;
        try {
            bank.getAccountByCprNumber(user.getCprNumber());
        } catch (BankServiceException_Exception e) {
            exceptionCatched = true;
        }
        assertTrue(exceptionCatched);

    }

    @Then("the service returns a failure message when retiring account")
    public void the_service_returns_a_failure_message_when_retiring_account() {
        boolean exceptionCatched = false;
        try {
            bank.retireAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
        } catch (BankServiceException_Exception e) {
            exceptionCatched = true;
        }
        assertTrue(exceptionCatched);

    }


    @When("the user gets an account that does not exist")
    public void the_user_gets_an_account_that_does_not_exist() {
        boolean exceptionCatched = false;
        if(bank.accountExists(user.getCprNumber()) ) {
            try {
                bank.retireAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
            } catch (BankServiceException_Exception e) {
                e.printStackTrace();
            }
        }
        try {
            bank.getAccountByCprNumber(user.getCprNumber());
        } catch (BankServiceException_Exception e) {
            exceptionCatched = true;
        }
        assertTrue(exceptionCatched);
    }

    @When("the user gets an account that does not exist by cpr number")
    public void the_user_gets_an_account_that_does_not_exist_by_cpr_number() {
        boolean exceptionCatched = false;
        if(bank.accountExists(user.getCprNumber()) ) {
            try {
                bank.retireAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
            } catch (BankServiceException_Exception e) {
                e.printStackTrace();
            }
        }
        try {
            bank.getAccountByCprNumber(user.getCprNumber());
        } catch (BankServiceException_Exception e) {
            exceptionCatched = true;
        }
        assertTrue(exceptionCatched);
    }

    @When("the user retires the account")
    public void the_user_retires_the_account() throws BankServiceException_Exception {
        bank.retireAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
        assertFalse(bank.accountExists(user.getCprNumber()));
    }

    @Then("the account is Null")
    public void the_account_is_Null() throws BankServiceException_Exception {
        boolean accountExists = false;
        if(bank.accountExists(user.getCprNumber()) ) {
            accountExists = true;
        }
        assertFalse(accountExists);
    }

    @When("the user retires an account that does not exist")
    public void the_user_retires_an_account_that_does_not_exist() {
        boolean exceptionCatched = false;
        if(bank.accountExists(user.getCprNumber()) ) {
            try {
                bank.retireAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
            } catch (BankServiceException_Exception e) {
                e.printStackTrace();
            }
        }
        try {
            bank.retireAccount(bank.getAccountByCprNumber(user.getCprNumber()).getId());
        } catch (BankServiceException_Exception e) {
            exceptionCatched = true;
        }
        assertTrue(exceptionCatched);
    }


    //FOR MULTIPLE USERS
    @Given("that the {string} has cprNumber {string}")
    public void that_the_has_cprNumber(String key, String cpr) throws BankServiceException_Exception {
        User tempUser = new User();
        tempUser.setCprNumber(cpr);
        if(users.containsKey(key)){
            users.get(key).setCprNumber(cpr);
        }else {
            users.put(key, tempUser);
        }
        assertEquals(users.get(key).getCprNumber(),cpr);
    }

    @Given("that the {string} has firstName {string}")
    public void that_the_has_firstName(String key, String firstName) throws BankServiceException_Exception {
        User tempUser = new User();
        tempUser.setFirstName(firstName);
        if(users.get(key).toString().length()>0){
            users.get(key).setFirstName(firstName);
        }else {
            users.put(key, tempUser);
        }
        assertEquals(users.get(key).getFirstName(),firstName);
    }

    @Given("that the {string} has lastName {string}")
    public void that_the_has_lastName(String key, String lastName) throws BankServiceException_Exception {
        User tempUser = new User();
        tempUser.setLastName(lastName);
        if(users.get(key).toString().length()>0){
            users.get(key).setLastName(lastName);
        }else {
            users.put(key, tempUser);
        }
        assertEquals(users.get(key).getLastName(),lastName);
    }

    @Given("that the balance of the {string} is {string}")
    public void that_the_balance_of_the_is(String key, String balanceInput) throws BankServiceException_Exception {
        User tempUser = users.get(key);
        bank.createAccountWithBalance(tempUser,new BigDecimal(Integer.parseInt(balanceInput)));
    }

    @Given("the accounts are created")
    public void the_accounts_are_created() throws BankServiceException_Exception {
        for(Map.Entry<String, User> entry : users.entrySet()) {
            String key = entry.getKey();
            User value = entry.getValue();
            assertTrue(bank.getAccountByCprNumber(value.getCprNumber()) != null);
        }
    }

    @When("the first user transfers money to the second user")
    public void the_first_user_transfers_money_to_the_second_user() throws BankServiceException_Exception {
        User user1 = users.get("first user");
        User user2 = users.get("second user");
        bank.transferMoneyFromTo(bank.getAccountByCprNumber(user1.getCprNumber()).getId(),
                bank.getAccountByCprNumber(user2.getCprNumber()).getId(),new BigDecimal(1000),"AgurkeTest - successfull transfere");
    }

    @Then("the money is transferred correctly")
    public void the_money_is_transferred_correctly() throws BankServiceException_Exception {
        User user2 = users.get("second user");
        assertEquals(bank.getAccountByCprNumber(user2.getCprNumber()).getBalance(),new BigDecimal(2000));
    }

    @When("the first user transfers money to the second resulting in a negative balance")
    public void the_first_user_transfers_money_to_the_second_resulting_in_a_negative_balance() {
        exceptionCatched = false;
        User user1 = users.get("first user");
        User user2 = users.get("second user");
        try {
            bank.transferMoneyFromTo(bank.getAccountByCprNumber(user1.getCprNumber()).getId(),
                    bank.getAccountByCprNumber(user2.getCprNumber()).getId(),new BigDecimal(10000),"AgurkeTest - successfull transfere");
        } catch (BankServiceException_Exception e) {
            exceptionCatched = true;
            System.out.print("Catched");
        }
    }

    @When("the first user transfers a negative amount of money to the second user")
    public void the_first_user_transfers_a_negative_amount_of_money_to_the_second_user()  {
        exceptionCatched = false;
        User user1 = users.get("first user");
        User user2 = users.get("second user");
        try {
            bank.transferMoneyFromTo(bank.getAccountByCprNumber(user1.getCprNumber()).getId(),
                    bank.getAccountByCprNumber(user2.getCprNumber()).getId(),new BigDecimal(-1000),"AgurkeTest - successfull transfere");

        } catch (BankServiceException_Exception e) {
            exceptionCatched = true;

        }
    }

    @Then("the service returns a failure message in regards to negative balance")
    public void the_service_returns_a_failure_message_in_regards_to_negative_balance() {
        assertTrue(exceptionCatched);
    }

    @Then("the service returns a failure message in regards to negative amount")
    public void the_service_returns_a_failure_message_in_regards_to_negative_amount() {
        assertTrue(exceptionCatched);
    }



}
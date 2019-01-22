Feature: Bank cucumber tests.
  This run all test in regards to the bank.


  Scenario: User creates account
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    When the user creates account
    Then the account is not "Null"
    And the account id is not ""

  Scenario: User gets account
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    And the account is created
    When the user gets account
    Then the account is not "Null"
    And the account id is correct
    And the account balance is "1000"
    And the account user is correct
    And the account transactions is empty

  Scenario: User gets account by cpr number
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    And the account is created
    When the user gets account by cpr number
    Then the account is not Null
    And the account id is correct
    And the account balance is "1000"
    And the account user is correct
    And the account transactions is empty


  Scenario: User gets accounts
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    And the account is created
    When the user gets accounts
    Then account infos is not "Null"
    And account infos is not empty
    And account info is not Null
    And account info user is correct



  Scenario: User creates account
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    When the user creates account
    Then the account id is not "Null"
    And the account id is not ""

  Scenario: User creates an account that already exists
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    When the user creates an account that already exists
    Then the service returns a failure message when creating account


  Scenario: User gets account
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    And the account is created
    When the user gets account
    Then the account is not Null
    And the account id is correct
    And the account balance is "1000"
    And the account user is correct
    And the account transactions is empty

  Scenario: User gets an account that does not exist
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    And the account is created
    When the user gets an account that does not exist
    Then the service returns a failure message when getting account


  Scenario: User gets account by cpr number
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    And the account is created
    When the user gets account by cpr number
    Then the account is not "Null"
    And the account id is correct
    And the account balance is "1000"
    And the account user is correct
    And the account transactions is empty

  Scenario: User gets an account that does not exist by cpr number
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    And the account is created
    When the user gets an account that does not exist by cpr number
    Then the service returns a failure message when getting account




  Scenario: User retires account
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    And the account is created
    When the user retires the account
    Then the account is Null

  Scenario: User retires an account that does not exist
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    And the account is created
    When the user retires an account that does not exist
    Then the service returns a failure message when retiring account

  Scenario: One user transfers money to a second user
    Given that the "first user" has cprNumber "995566-2233"
    And that the "first user" has firstName "firstName"
    And that the "first user" has lastName "lastName"
    And that the balance of the "first user" is "1000"
    And that the "second user" has cprNumber "662288-5522"
    And that the "second user" has firstName "firstName"
    And that the "second user" has lastName "lastName"
    And that the balance of the "second user" is "1000"
    And the accounts are created
    When the first user transfers money to the second user
    Then the money is transferred correctly

  Scenario: One user transfers money to another user resulting in a negative balance
    Given that the "first user" has cprNumber "995566-2233"
    And that the "first user" has firstName "firstName"
    And that the "first user" has lastName "lastName"
    And that the balance of the "first user" is "1000"
    And that the "second user" has cprNumber "662288-5522"
    And that the "second user" has firstName "firstName"
    And that the "second user" has lastName "lastName"
    And that the balance of the "second user" is "1000"
    And the accounts are created
    When the first user transfers money to the second resulting in a negative balance
    Then the service returns a failure message in regards to negative balance

  Scenario: One user transfers a negative amount of money to another user
    Given that the "first user" has cprNumber "995566-2233"
    And that the "first user" has firstName "firstName"
    And that the "first user" has lastName "lastName"
    And that the balance of the "first user" is "1000"
    And that the "second user" has cprNumber "662288-5522"
    And that the "second user" has firstName "firstName"
    And that the "second user" has lastName "lastName"
    And that the balance of the "second user" is "1000"
    And the accounts are created
    When the first user transfers a negative amount of money to the second user
    Then the service returns a failure message in regards to negative amount
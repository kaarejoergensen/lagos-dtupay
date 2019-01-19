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
    When the user gets accounts
    Then account infos is not "Null"
    And account infos is not empty
    And account info is not Null
    And account info user is correct
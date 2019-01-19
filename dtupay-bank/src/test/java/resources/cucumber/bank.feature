Feature: Bank cucumber tests.
  This run all test in regards to the bank.

  Scenario: Sunday isn't Friday
    Given today is Sunday
    When I ask whether it's Friday yet
    Then I should be told "Nope"


  Scenario: User creates account
    Given that the user has cprNumber "995566-2233"
    And that the user has firstName "firstName"
    And that the user has lastName "lastName"
    And that the balance is "1000"
    When the user creates account
    Then the account is not "Null"
    And the account id is not ""
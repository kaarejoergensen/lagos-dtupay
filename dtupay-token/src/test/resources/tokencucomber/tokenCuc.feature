Feature: Token and Storage test
  Check if the different features in Token API run as intended

  Scenario: Issue A token
    Given the user id, user name and the number of tokens
    When issuing tokens
    Then the tokens provided should be "5"



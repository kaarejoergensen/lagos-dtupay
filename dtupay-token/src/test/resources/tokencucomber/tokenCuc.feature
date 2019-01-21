Feature: Token and Storage test
  Check if the different features in Token API run as intended

  Scenario: Standard token issuing and using
    Given the user id, user name and the number of tokens
    When issuing tokens
    Then the tokens is not null
    And the token count provided should be "5"
    And the user is able to use the tokens
    And not use tokens beyond the provided ones

   Scenario: Tampering Tokens
    Given username "bro", userId "8273" and "1" number of tokens
    When retreving a set of tokens
    Then the token count is indeed "1"
    And the user can't tamper the token  - use a non existing token by injecting userid



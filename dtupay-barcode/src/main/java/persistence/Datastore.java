package persistence;

public interface Datastore {
    void useToken(String tokenString);

    boolean isTokenUsed(String tokenString);

    void addTokens(int tokens, String userName);

    int getNumberOfUnusedTokens(String userName);

    int getTotalNumberOfTokensIssued();

    void setTotalNumberOfTokensIssued(int totalNumberOfTokensIssued);
}

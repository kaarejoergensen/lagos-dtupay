package persistence;

import tokens.TokenProvider;

import java.util.*;

public class MemoryDataStore implements Datastore {
    private int totalNumberOfTokensIssued;
    private TokenProvider tokenProvider;
    private Map<String, Integer> numberOfUnusedTokenMap;
    private Set<String> usedTokens;

    public MemoryDataStore(TokenProvider tokenProvider) {
        this.totalNumberOfTokensIssued = 0;
        this.tokenProvider = tokenProvider;
        this.numberOfUnusedTokenMap = new HashMap<>();
        this.usedTokens = new HashSet<>();
    }

    @Override
    public void useToken(String tokenString) {
        String userName = this.tokenProvider.getUserName(tokenString);
        Integer numberOfUnusedTokens = this.numberOfUnusedTokenMap.get(userName);
        if (numberOfUnusedTokens == null)
            throw new IllegalArgumentException("The token does not exist");
        this.usedTokens.add(tokenString);
        this.numberOfUnusedTokenMap.put(userName, numberOfUnusedTokens - 1);
    }

    @Override
    public boolean isTokenUsed(String tokenString) {
        return this.usedTokens.contains(tokenString);
    }

    @Override
    public void addTokens(int tokens, String userName) {
        Integer numberOfUnusedTokens = this.numberOfUnusedTokenMap.get(userName);
        if (numberOfUnusedTokens == null)
            numberOfUnusedTokens = 0;
        numberOfUnusedTokens += tokens;
        this.numberOfUnusedTokenMap.put(userName, numberOfUnusedTokens);
    }

    @Override
    public int getNumberOfUnusedTokens(String userName) {
        Integer numberOfUnusedTokens = this.numberOfUnusedTokenMap.get(userName);
        return numberOfUnusedTokens != null ? numberOfUnusedTokens : 0;
    }

    @Override
    public int getTotalNumberOfTokensIssued() {
        return totalNumberOfTokensIssued;
    }

    @Override
    public void setTotalNumberOfTokensIssued(int totalNumberOfTokensIssued) {
        this.totalNumberOfTokensIssued = totalNumberOfTokensIssued;
    }
}

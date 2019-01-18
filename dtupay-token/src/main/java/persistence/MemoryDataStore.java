package persistence;

import java.util.*;

public class MemoryDataStore implements Datastore {
    private Map<String, Integer> numberOfUnUsedTokensMap;

    private Set<String> unUsedTokens;
    private Set<String> usedTokens;

    public MemoryDataStore() {
        this.numberOfUnUsedTokensMap = new HashMap<>();

        this.usedTokens = new HashSet<>();
        this.unUsedTokens = new HashSet<>();
    }

    @Override
    public void addTokens(Set<String> newTokens, String userId) {
        Integer numberOfUnusedTokens = numberOfUnUsedTokensMap.get(userId);
        if (numberOfUnusedTokens == null)
            numberOfUnusedTokens = 0;
        numberOfUnusedTokens += newTokens.size();
        this.numberOfUnUsedTokensMap.put(userId, numberOfUnusedTokens);
        this.unUsedTokens.addAll(newTokens);
    }

    @Override
    public void useToken(String token, String userId) {
        this.unUsedTokens.remove(token);
        this.usedTokens.add(token);
        Integer numberOfUnusedTokens = numberOfUnUsedTokensMap.get(userId);
        if (numberOfUnusedTokens == null)
            throw new IllegalArgumentException("Should never happen");
        numberOfUnusedTokens -= 1;
        this.numberOfUnUsedTokensMap.put(userId, numberOfUnusedTokens);
    }

    @Override
    public int getNumberOfUnusedTokens(String userId) {
        Integer numberOfUnusedTokens = numberOfUnUsedTokensMap.get(userId);
        return numberOfUnusedTokens == null ? 0 : numberOfUnusedTokens;
    }

    @Override
    public boolean isTokenUnique(String token) {
        return !this.usedTokens.contains(token) && !this.unUsedTokens.contains(token);
    }

    @Override
    public boolean checkToken(String token) {
        return this.unUsedTokens.contains(token) && !this.usedTokens.contains(token);
    }
}

package persistence;

import java.util.Set;

public interface Datastore {
    void addTokens(Set<String> tokens, String userId);

    void useToken(String token, String userId);

    int getNumberOfUnusedTokens(String userId);

    boolean isTokenUnique(String token);

    boolean checkToken(String token);
}

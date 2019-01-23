package persistence;

import javax.crypto.SecretKey;
import java.util.Set;
/**
 * @author Kåre
 */
public interface Datastore {
    SecretKey getSecretKey();

    void addTokens(Set<String> tokens, String userId);

    void useToken(String token, String userId);

    int getNumberOfUnusedTokens(String userId);

    boolean isTokenUnique(String token);

    boolean checkToken(String token);

    void reset();
}

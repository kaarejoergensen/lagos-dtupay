package tokens;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import persistence.Datastore;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author Kåre
 */
public class TokenProvider {
    private Key key;

    private Datastore datastore;

    public TokenProvider(Datastore datastore) {
        this.datastore = datastore;
        this.key = datastore.getSecretKey();
    }

    public Set<String> getTokens(String userName, String userId, int numberOfTokens) throws IllegalArgumentException {
        if (numberOfTokens < 1 || numberOfTokens > 5)
            throw new IllegalArgumentException("The number of tokens must be between 1 and 5");
        int numberOfUnusedTokens = this.datastore.getNumberOfUnusedTokens(userId);
        if (numberOfUnusedTokens > 1)
            throw new IllegalArgumentException("Number of unused tokens is more than 1");
        Set<String> uuids = new HashSet<>();
        for (int i = 0; i < numberOfTokens; i++) {
            String uuid;
            do {
                uuid = UUID.randomUUID().toString();
            } while (!uuids.contains(uuid) && !datastore.isTokenUnique(uuid));

            uuids.add(uuid);
        }
        this.datastore.addTokens(uuids, userId);

        return uuids.stream().map(u -> issueToken(userName, userId, u)).collect(Collectors.toSet());
    }

    public boolean useToken(String tokenString) {
        Optional<Jws<Claims>> claims = this.checkToken(tokenString);
        if (claims.isPresent()) {
            String userId = this.getUserId(claims.get());
            String uuid = this.getUUID(claims.get());
            if (this.datastore.checkToken(uuid)) {
                this.datastore.useToken(uuid, userId);
                return true;
            }
        }
        return false;
    }

    public void reset() {
        this.datastore.reset();
    }

    public Optional<String> getUserIdFromToken(String token) {
        Optional<Jws<Claims>> claims = this.checkToken(token);
        return claims.map(this::getUserId);
    }

    private String issueToken(String userName, String userId, String UUID) {
        LocalDateTime expiration = LocalDateTime.now().plusDays(7);
        Date out = Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(userName)
                .setId(userId)
                .claim("UUID", UUID)
                .setExpiration(out).signWith(key).compact();
    }

    private Optional<Jws<Claims>> checkToken(String tokenString) {
        Jws<Claims> claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(tokenString);
        } catch(SignatureException | MalformedJwtException e) {
            System.err.println("Token invalid: " + e.getMessage());
            return Optional.empty();
        }
        return Optional.of(claims);
    }

    private String getUUID(Jws<Claims> claims) {
        return (String) claims.getBody().get("UUID");
    }

    private String getUserId(Jws<Claims> claims) {
        return claims.getBody().getId();
    }
}

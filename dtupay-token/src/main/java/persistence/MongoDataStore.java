package persistence;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.bson.Document;
import org.bson.types.Binary;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
/**
 * @author Fredrik
 */
public class MongoDataStore implements Datastore{
    private MongoDatabase mdb;

    public MongoDataStore() throws IOException {
        this(ServerAddress.defaultHost(),ServerAddress.defaultPort());
    }

    public MongoDataStore(String host) throws IOException {
        this(host, ServerAddress.defaultPort());
    }

    public MongoDataStore(String host, int port) {
        boolean connectionSuccessful = false;
        for (int numberOfTries = 12; numberOfTries > 0; numberOfTries--) {
            try {
                MongoClientOptions mongoClientOptions = MongoClientOptions.builder().serverSelectionTimeout(500).build();
                MongoClient client = new MongoClient(new ServerAddress(host, port), mongoClientOptions);
                mdb = client.getDatabase("dtupay");
                this.reset();
                System.err.println("Connection to mongo host '" + host + "' suceeded");
                connectionSuccessful = true;
                break;
            } catch (Exception e) {
                System.err.println("Connection to mongo host '" + host + "' failed, sleeping 5 seconds");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (!connectionSuccessful) throw new RuntimeException("Could not connect to mongo host '" + host + "'");
    }

    @Override
    public SecretKey getSecretKey() {
        MongoCollection secretKeyCollection = mdb.getCollection("Key");
        Document key = (Document) secretKeyCollection.find().first();
        if (key == null || key.get("key") == null) {
            SecretKey generatedKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            Document newKey = new Document();
            newKey.append("key", Base64.getEncoder().encode(generatedKey.getEncoded()));
            secretKeyCollection.insertOne(newKey);
            return generatedKey;
        } else {
            return Keys.hmacShaKeyFor(((Binary)key.get("key")).getData());
        }
    }

    @Override
    public void addTokens(Set<String> tokens, String userId) {
        MongoCollection unusedTokenCollection = mdb.getCollection("UnusedTokens");
        for(String s : tokens){
            Document newToken = new Document();
            newToken.append("userId", userId);
            newToken.append("token", s);
            unusedTokenCollection.insertOne(newToken);
        }
    }

    @Override
    public void useToken(String token, String userId){
        MongoCollection unusedTokenCollection = mdb.getCollection("UnusedTokens");

        ArrayList<BasicDBObject> queryList = new ArrayList<>();
        queryList.add(new BasicDBObject("userId", userId));
        queryList.add(new BasicDBObject("token", token));
        BasicDBObject query = new BasicDBObject("$and", queryList);
        Document extractedToken = (Document)unusedTokenCollection.find(query).first();
        if(extractedToken == null) throw new IllegalArgumentException("Token does not exist");
        unusedTokenCollection.deleteOne(extractedToken);

        MongoCollection usedTokenCollection = mdb.getCollection("UsedTokens");
        usedTokenCollection.insertOne(extractedToken);
    }

    public int getNumberOfUnusedTokens(String userId){
        MongoCursor<Document> cursor = mdb.getCollection("UnusedTokens").find(eq("userId",userId)).iterator();
        return cursorSize(cursor);
    }

    @Override
    public boolean isTokenUnique(String token) {
        MongoCollection unusedTokenCollection = mdb.getCollection("UnusedTokens");
        MongoCollection usedTokenCollection = mdb.getCollection("UsedTokens");

        BasicDBObject query = new BasicDBObject();
        query.put("token", token);

        int unusedTokenSize = cursorSize(unusedTokenCollection.find(query).iterator());
        int usedTokenSize = cursorSize(usedTokenCollection.find(query).iterator());

        return unusedTokenSize == 0 && usedTokenSize == 0;
    }

    @Override
    public boolean checkToken(String token) {
        MongoCollection unusedTokenCollection = mdb.getCollection("UnusedTokens");
        MongoCollection usedTokenCollection = mdb.getCollection("UsedTokens");

        BasicDBObject query = new BasicDBObject();
        query.put("token", token);

        int unusedTokenSize = cursorSize(unusedTokenCollection.find(query).iterator());
        int usedTokenSize = cursorSize(usedTokenCollection.find(query).iterator());

        return unusedTokenSize == 1 && usedTokenSize == 0;
    }

    @Override
    public void reset() {
        mdb.getCollection("UnusedTokens").drop();
        mdb.getCollection("UsedTokens").drop();
        System.out.println("Datastore erased");
    }

    private int cursorSize(MongoCursor cursor){
        int count = 0;
        try {
            while (cursor.hasNext()) {
                count++;
                cursor.next();
            }
        } finally {
            cursor.close();
        }
        return count;
    }

}

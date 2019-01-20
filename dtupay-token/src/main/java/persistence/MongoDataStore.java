package persistence;

import java.util.*;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.bson.Document;
import org.bson.types.Binary;

import javax.crypto.SecretKey;

import static com.mongodb.client.model.Filters.eq;

public class MongoDataStore implements Datastore{
    private MongoDatabase mdb;

    public MongoDataStore() {
        this(ServerAddress.defaultHost());
    }

    public MongoDataStore(String host){
        MongoCredential mongoCredential = MongoCredential.createCredential("root",
                "admin",
                "rootPassXXX".toCharArray());
        MongoClient client = new MongoClient(new ServerAddress(host), Collections.singletonList(mongoCredential));
        mdb = client.getDatabase("dtupay");
        System.out.println("Successfully connected to Mongo Token Datastore");
        this.reset();
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

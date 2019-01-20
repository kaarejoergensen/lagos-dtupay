package persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class MongoDataStore implements  Datastore{

    private MongoClient client;
    private MongoDatabase mdb;

    public MongoDataStore(){
        client = new MongoClient("localhost", 27017);
        mdb = client.getDatabase("TokenDatastore");
        System.out.println("Successfully connected to Mongo Token Datastore");
        clearDatabse();
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

        ArrayList queryList = new ArrayList<>();
        queryList.add(new BasicDBObject("userId", userId));
        queryList.add(new BasicDBObject("token", token));
        BasicDBObject query = new BasicDBObject("$and", queryList);
        Document extractedToken = (Document)unusedTokenCollection.find(query).first();
        if(extractedToken == null) throw new IllegalArgumentException("Token does not exist"); //Fant ingen tokens bruh
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
        this.clearDatabse();
    }


    private void clearDatabse(){
        mdb.getCollection("UnusedTokens").drop();
        mdb.getCollection("UsedTokens").drop();
        System.out.println("Datastore erased");
    }

    private int cursorSize(MongoCursor cursor){
        int count = 0;
        try {
            while (cursor.hasNext()) {
                count++;
            }
        } finally {
            cursor.close();
        }
        return count;
    }

}

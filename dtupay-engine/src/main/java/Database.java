import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import models.Roles;
import models.User;
import org.bson.Document;


import java.util.Arrays;


import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Database{

    private MongoClient client;
    private MongoDatabase mdb;

    public Database(){
            client = new MongoClient("localhost", 27017);
            mdb = client.getDatabase("dtupay");
            System.out.println("Succesfully connected to database 'dtupay'");

            clearUsers();
    }

    public void createUser() {

    }


    public String getUser() {

        MongoCollection collection = mdb.getCollection("Account");

        Document doc = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
                .append("info", new Document("x", 203).append("y", 102));

        collection.insertOne(doc);

        System.out.println(collection.find().first().toString());
        return collection.toString();
    }

    public void addToken(String userId, String webtoken){
        MongoCollection tokenCollection = mdb.getCollection("Tokens");
        tokenCollection.insertOne(new Document("userId", userId).append("token",webtoken));
    }


    public List<String> getTokens(String userId) {

        MongoCollection collection = mdb.getCollection("Tokens");

        MongoCursor<Document> cursor = collection.find(eq("userId", userId)).iterator();

        List<String> tokens = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                tokens.add(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
        return tokens;
    }

    public void addUser(User user){

        /*
        TODO: Check if user already exist
         */

        MongoCollection users = mdb.getCollection("Users");

        Document doc = new Document();
        doc.append("cprNumber", user.getCprNumber());
        doc.append("firstname", user.getFirstName());
        doc.append("lastname", user.getLastName());
        doc.append("role", user.getRole().getIdentificationNumber());

        users.insertOne(doc);
    }

    public User getUser(String cprNumber){
        MongoCollection collection = mdb.getCollection("Users");
        Document doc = (Document)collection.find(eq("cprNumber", cprNumber)).first();
        User u = new User();
        u.setFirstName(doc.get("firstname").toString());
        u.setLastName(doc.get("lastname").toString());
        u.setCprNumber(doc.get("cprNumber").toString());
        u.setRole(Roles.getRole(Integer.parseInt(doc.get("role").toString())));
        return u;
    }


    public void clearUsers(){
        mdb.getCollection("Users").drop();
        System.out.println("All users erased");
    }



}

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;


public class Database{

    private MongoClient client;

    public Database(){
        client = new MongoClient( "localhost" , 27017);

    }



    public void createUser() {

    }


    public String getUser() {
        MongoDatabase database = client.getDatabase("dtupay");
        MongoCollection collection = database.getCollection("Account");
        return collection.toString();
    }


    public String getUsedTokens() {
        return null;
    }
}

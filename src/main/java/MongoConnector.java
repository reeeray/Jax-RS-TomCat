import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoConnector {

    public static void main(String ... args) {
        //complex object, stored all connections inside and hard to create, better to do it once and then reuse
        //it has a few constructors, new MongoCLient() - default, trying to connect default port 27017
        //check validity of th entered data : new MongoCLient(new ServerAddress("localhost", 27017), MongoClientOptions.class)
        //MongoClientOptions.class - additional settings of mongo driver
        //check validity of th entered data : new MongoCLient(new MongoCLientURI("mongodb://localhost:27017"))
        final MongoClientOptions mco = MongoClientOptions.builder().connectionsPerHost(100).build();
        final MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017), mco);
        final MongoDatabase mdb = mongoClient.getDatabase("courses");
        //has the same api as mongodb shell client
        //mongo stored data in BSON format - Binary JSON representation
        final MongoCollection<Document> group  =mdb.getCollection("java");
        for(Document d : group.find()) {
            System.out.println(d);
        }
    }
}

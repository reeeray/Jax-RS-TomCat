package ru.mail.sheingrisha.core;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.Random;

@Path("links")
public class LinkShorter {

    public static final int NUMBER_OF_ATTEMPTS = 5;
    private static final MongoCollection<Document> LINKS_COLLECTION;

    static {
        final MongoClientOptions mco = MongoClientOptions.builder().connectionsPerHost(100).build();
        final MongoClient mc = new MongoClient(new ServerAddress("localhost", 27017), mco);
        final MongoDatabase mdb = mc.getDatabase("courses");
        LINKS_COLLECTION = mdb.getCollection("links");
    }

    private static String generateStringId(final int idLength) {
        final String possibleCharacters = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";
        final StringBuilder resultID = new StringBuilder();
        final Random rd = new Random();
        while (resultID.length() < idLength) {
            final int pos = (int) (rd.nextFloat() * possibleCharacters.length());
            resultID.append(possibleCharacters.charAt(pos));
        }
        return resultID.toString();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{id}")
    public Response getUrlById(final @PathParam("id") String id) {
        if (id == null || id.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        final FindIterable<Document> linksITerator = LINKS_COLLECTION.find(new Document("id", id));
        final Iterator<Document> links = linksITerator.iterator();
        if (!links.hasNext()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        final String url = links.next().getString("url");
        if (url == null || url.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(url).build();
    }

    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response shortLink(final String longLink) {
        int nrOfAttempt = 0;
        while (nrOfAttempt < NUMBER_OF_ATTEMPTS) {
            final String id = generateStringId(NUMBER_OF_ATTEMPTS);
            final Document docToSave = new Document("id", id);
            docToSave.put("url", longLink);
            try {
                LINKS_COLLECTION.insertOne(docToSave);
                return Response.ok(id).build();
            } catch (final MongoWriteException e) {
                System.out.println("Exception! ID already exists! Common number of attempts : "
                        + NUMBER_OF_ATTEMPTS + " Attempt number : " + nrOfAttempt);
            }
            nrOfAttempt++;
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}

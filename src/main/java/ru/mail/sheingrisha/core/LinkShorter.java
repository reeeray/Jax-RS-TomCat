package ru.mail.sheingrisha.core;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Path("links")
public class LinkShorter {

    private static final AtomicInteger incrementor = new AtomicInteger();

    private static Map<String, String> storage = new ConcurrentHashMap<>();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{id}")
    public Response getUrlById (final @PathParam("id") String id) {
        if(id == null || id.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        final String url = storage.get(id);
        if(url == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(url).build();
    }

    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response shortLink(final String longLink) {
        final String id = String.valueOf(incrementor.getAndIncrement());
        storage.put(id, longLink);
        return Response.ok(id).build();
    }
}

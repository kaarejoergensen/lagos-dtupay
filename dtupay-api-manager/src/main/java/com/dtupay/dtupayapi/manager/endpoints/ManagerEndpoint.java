package com.dtupay.dtupayapi.manager.endpoints;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/v1/manager")
public class ManagerEndpoint {

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        return Response.ok().build();
    }
}

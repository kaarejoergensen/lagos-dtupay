package com.dtupay.dtupayapi.manager.endpoints;


import clients.BankClient;
import clients.TokenClient;
import com.dtupay.dtupayapi.manager.Models.UserModel;
import com.dtupay.dtupayapi.manager.application.ManagerUtils;
import exceptions.ClientException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 @author Thomas
 */

@Path("/v1/manager")
public class ManagerEndpoint {

    private TokenClient tokenClient;
    private BankClient bankClient;
    private ManagerUtils utils;

    public void setRabbitMQInfo(String host, String username, String password) throws IOException, TimeoutException {
        if (host == null || username == null || password == null)
            throw new IllegalArgumentException("No arguments can be null!");
        this.tokenClient = new TokenClient(host, username, password);
        this.bankClient = new BankClient(host, username, password);
        utils = new ManagerUtils(tokenClient, bankClient);
    }

    @POST
    @Path("/createAccount")
    public Response createAccount(){
        return null;
    }

    /*

     */

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(){
        try {
            List<UserModel> users = utils.getAllUsers();
            return Response.ok().entity(users).build();
        } catch (ClientException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("").build();
        }
    }

    /*
        Getting user - fetching the user model and list of transactions
     */

    @GET
    @Path("/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("userId") String userId) {
        try {
            UserModel model = utils.getUser(userId);
            return Response.ok().entity(model).build();
        } catch (ClientException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Could not find makker").build();
        }
    }

    @POST
    @Path("/user/retireAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retireAccount(@QueryParam("accountId") String accountId) {
        try {
            this.bankClient.retireAccount(accountId);
            return Response.status(Response.Status.OK).build();
        } catch (ClientException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}

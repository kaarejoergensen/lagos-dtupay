package com.dtupay.dtupayapi.manager.endpoints;


import clients.BankClient;
import clients.TokenClient;
import exceptions.ClientException;
import models.Transaction;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;


@Path("/v1/manager")
public class ManagerEndpoint {

    private TokenClient tokenClient;
    private BankClient bankClient;

    public void setRabbitMQInfo(String host, String username, String password) throws IOException, TimeoutException {
        this.tokenClient = new TokenClient(host, username, password);
        this.bankClient = new BankClient(host, username, password);
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@QueryParam("userId") String userId) {
        List <Transaction> transactions;
        try {
            transactions = bankClient.getAccount(userId).getTransactions();
            return Response.ok().entity(transactions).build();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return Response.serverError().build();
    }
}
